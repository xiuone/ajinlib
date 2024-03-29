package xy.xy.base.utils.record.record.recorder;

import android.annotation.SuppressLint;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;


import xy.xy.base.utils.Logger;
import xy.xy.base.utils.record.record.recorder.fftlib.FftFactory;
import xy.xy.base.utils.record.record.recorder.listener.RecordDataListener;
import xy.xy.base.utils.record.record.recorder.listener.RecordFftDataListener;
import xy.xy.base.utils.record.record.recorder.listener.RecordResultListener;
import xy.xy.base.utils.record.record.recorder.listener.RecordSoundSizeListener;
import xy.xy.base.utils.record.record.recorder.listener.RecordStateListener;
import xy.xy.base.utils.record.record.recorder.mp3.Mp3EncodeThread;
import xy.xy.base.utils.record.record.recorder.wav.WavUtils;
import xy.xy.base.utils.record.record.utils.ByteUtils;
import xy.xy.base.utils.record.record.utils.FileUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import xy.xy.base.utils.Logger;
import xy.xy.base.utils.record.record.recorder.listener.RecordDataListener;
import xy.xy.base.utils.record.record.recorder.listener.RecordFftDataListener;
import xy.xy.base.utils.record.record.recorder.listener.RecordResultListener;
import xy.xy.base.utils.record.record.recorder.listener.RecordSoundSizeListener;
import xy.xy.base.utils.record.record.recorder.listener.RecordStateListener;
import xy.xy.base.utils.record.record.recorder.mp3.Mp3EncodeThread;
import xy.xy.base.utils.record.record.recorder.wav.WavUtils;


/**
 * @author zhaolewei on 2018/7/10.
 */
public class RecordHelper {
    private static final String TAG = RecordHelper.class.getSimpleName();
    private volatile static RecordHelper instance;
    private volatile RecordState state = RecordState.IDLE;
    private static final int RECORD_AUDIO_BUFFER_TIMES = 1;

    private RecordStateListener recordStateListener;
    private RecordDataListener recordDataListener;
    private RecordSoundSizeListener recordSoundSizeListener;
    private RecordResultListener recordResultListener;
    private RecordFftDataListener recordFftDataListener;
    private RecordConfig currentConfig;
    private AudioRecordThread audioRecordThread;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private File resultFile = null;
    private File tmpFile = null;
    private final List<File> files = new ArrayList<>();
    private Mp3EncodeThread mp3EncodeThread;

    private RecordHelper() {
    }

    static RecordHelper getInstance() {
        if (instance == null) {
            synchronized (RecordHelper.class) {
                if (instance == null) {
                    instance = new RecordHelper();
                }
            }
        }
        return instance;
    }

    RecordState getState() {
        return state;
    }

    void setRecordStateListener(RecordStateListener recordStateListener) {
        this.recordStateListener = recordStateListener;
    }

    void setRecordDataListener(RecordDataListener recordDataListener) {
        this.recordDataListener = recordDataListener;
    }

    void setRecordSoundSizeListener(RecordSoundSizeListener recordSoundSizeListener) {
        this.recordSoundSizeListener = recordSoundSizeListener;
    }

    void setRecordResultListener(RecordResultListener recordResultListener) {
        this.recordResultListener = recordResultListener;
    }

    public void setRecordFftDataListener(RecordFftDataListener recordFftDataListener) {
        this.recordFftDataListener = recordFftDataListener;
    }

    public void start(String filePath, RecordConfig config) {
        this.currentConfig = config;
        if (state != RecordState.IDLE && state != RecordState.STOP) {
            Logger.INSTANCE.e(TAG, "状态异常当前状态： ${state.name()}");
            return;
        }
        resultFile = new File(filePath);
        String tempFilePath = getTempFilePath();

        Logger.INSTANCE.d(TAG, "----------------开始录制 %s------------------------"+ currentConfig.getFormat().name());
        Logger.INSTANCE.d(TAG, "参数： "+ currentConfig.toString());
        Logger.INSTANCE.i(TAG, "pcm缓存 tmpFile: "+ tempFilePath);
        Logger.INSTANCE.i(TAG, "录音文件 resultFile: "+ filePath);


        tmpFile = new File(tempFilePath);
        audioRecordThread = new AudioRecordThread();
        audioRecordThread.start();
    }

    public void stop() {
        if (state == RecordState.IDLE) {
            Logger.INSTANCE.e(TAG, "状态异常当前状态： "+ state.name());
            return;
        }

        if (state == RecordState.PAUSE) {
            makeFile();
            state = RecordState.IDLE;
            notifyState();
            stopMp3Encoded();
        } else {
            state = RecordState.STOP;
            notifyState();
        }
    }

    void pause() {
        if (state != RecordState.RECORDING) {
            Logger.INSTANCE.e(TAG, "状态异常当前状态： "+ state.name());
            return;
        }
        state = RecordState.PAUSE;
        notifyState();
    }

    void resume() {
        if (state != RecordState.PAUSE) {
            Logger.INSTANCE.e(TAG, "状态异常当前状态： "+ state.name());
            return;
        }
        String tempFilePath = getTempFilePath();
        Logger.INSTANCE.i(TAG, "tmpPCM File: "+tempFilePath);
        tmpFile = new File(tempFilePath);
        audioRecordThread = new AudioRecordThread();
        audioRecordThread.start();
    }

    private void notifyState() {
        if (recordStateListener == null) {
            return;
        }
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                recordStateListener.onStateChange(state);
            }
        });

        if (state == RecordState.STOP || state == RecordState.PAUSE) {
            if (recordSoundSizeListener != null) {
                recordSoundSizeListener.onSoundSize(0);
            }
        }
    }

    private void notifyFinish() {
        Logger.INSTANCE.d(TAG, "录音结束 file: "+resultFile.getAbsolutePath());

        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (recordStateListener != null) {
                    recordStateListener.onStateChange(RecordState.FINISH);
                }
                if (recordResultListener != null) {
                    recordResultListener.onResult(resultFile);
                }
            }
        });
    }

    private void notifyError(final String error) {
        if (recordStateListener == null) {
            return;
        }
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                recordStateListener.onStateChange(RecordState.ERROR);
            }
        });
    }

    private final FftFactory fftFactory = new FftFactory(FftFactory.Level.Original);

    private void notifyData(final byte[] data) {
        if (recordDataListener == null && recordSoundSizeListener == null && recordFftDataListener == null) {
            return;
        }
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (recordDataListener != null) {
                    recordDataListener.onData(data);
                }

                if (recordFftDataListener != null || recordSoundSizeListener != null) {
                    byte[] fftData = fftFactory.makeFftData(data);
                    if (fftData != null) {
                        if (recordSoundSizeListener != null) {
                            recordSoundSizeListener.onSoundSize(getDb(fftData));
                        }
                        if (recordFftDataListener != null) {
                            recordFftDataListener.onFftData(fftData);
                        }
                    }
                }
            }
        });
    }

    private int getDb(byte[] data) {
        double sum = 0;
        double ave;
        int length = Math.min(data.length, 128);
        int offsetStart = 0;
        for (int i = offsetStart; i < length; i++) {
            sum += data[i] * data[i];
        }
        ave = sum / (length - offsetStart) ;
        return (int) (Math.log10(ave) * 20);
    }

    private void initMp3EncoderThread(int bufferSize) {
        try {
            mp3EncodeThread = new Mp3EncodeThread(resultFile, bufferSize);
            mp3EncodeThread.start();
        } catch (Exception e) {
            Logger.INSTANCE.e(TAG, e.getMessage());
        }
    }

    private class AudioRecordThread extends Thread {
        private final AudioRecord audioRecord;
        private final int bufferSize;

        @SuppressLint("MissingPermission")
        AudioRecordThread() {
            bufferSize = AudioRecord.getMinBufferSize(currentConfig.getSampleRate(),
                    currentConfig.getChannelConfig(), currentConfig.getEncodingConfig()) * RECORD_AUDIO_BUFFER_TIMES;
            Logger.INSTANCE.d(TAG, "record buffer size = "+ bufferSize);
            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, currentConfig.getSampleRate(),
                    currentConfig.getChannelConfig(), currentConfig.getEncodingConfig(), bufferSize);
            if (currentConfig.getFormat() == RecordConfig.RecordFormat.MP3) {
                if (mp3EncodeThread == null) {
                    initMp3EncoderThread(bufferSize);
                } else {
                    Logger.INSTANCE.e(TAG, "mp3EncodeThread != null, 请检查代码");
                }
            }
        }

        @Override
        public void run() {
            super.run();

            switch (currentConfig.getFormat()) {
                case MP3:
                    startMp3Recorder();
                    break;
                default:
                    startPcmRecorder();
                    break;
            }
        }

        private void startPcmRecorder() {
            state = RecordState.RECORDING;
            notifyState();
            Logger.INSTANCE.d(TAG, "开始录制 Pcm");
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(tmpFile);
                audioRecord.startRecording();
                byte[] byteBuffer = new byte[bufferSize];

                while (state == RecordState.RECORDING) {
                    int end = audioRecord.read(byteBuffer, 0, byteBuffer.length);
                    notifyData(byteBuffer);
                    fos.write(byteBuffer, 0, end);
                    fos.flush();
                }
                audioRecord.stop();
                files.add(tmpFile);
                if (state == RecordState.STOP) {
                    makeFile();
                } else {
                    Logger.INSTANCE.i(TAG, "暂停！");
                }
            } catch (Exception e) {
                Logger.INSTANCE.e(TAG, e.getMessage());
                notifyError("录音失败");
            } finally {
                try {
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (state != RecordState.PAUSE) {
                state = RecordState.IDLE;
                notifyState();
                Logger.INSTANCE.d(TAG, "录音结束");
            }
        }

        private void startMp3Recorder() {
            state = RecordState.RECORDING;
            notifyState();
            try {
                audioRecord.startRecording();
                short[] byteBuffer = new short[bufferSize];

                while (state == RecordState.RECORDING) {
                    int end = audioRecord.read(byteBuffer, 0, byteBuffer.length);
                    if (mp3EncodeThread != null) {
                        mp3EncodeThread.addChangeBuffer(new Mp3EncodeThread.ChangeBuffer(byteBuffer, end));
                    }
                    notifyData(ByteUtils.toBytes(byteBuffer));
                }
                audioRecord.stop();
            } catch (Exception e) {
                Logger.INSTANCE.e(TAG, e.getMessage());
                notifyError("录音失败");
            }
            if (state != RecordState.PAUSE) {
                state = RecordState.IDLE;
                notifyState();
                stopMp3Encoded();
            } else {
                Logger.INSTANCE.d(TAG, "暂停");
            }
        }
    }

    private void stopMp3Encoded() {
        if (mp3EncodeThread != null) {
            mp3EncodeThread.stopSafe(new Mp3EncodeThread.EncordFinishListener() {
                @Override
                public void onFinish() {
                    notifyFinish();
                    mp3EncodeThread = null;
                }
            });
        } else {
            Logger.INSTANCE.e(TAG, "mp3EncodeThread is null, 代码业务流程有误，请检查！！ ");
        }
    }

    private void makeFile() {
        switch (currentConfig.getFormat()) {
            case MP3:
                return;
            case WAV:
                mergePcmFile();
                makeWav();
                break;
            case PCM:
                mergePcmFile();
                break;
            default:
                break;
        }
        notifyFinish();
        Logger.INSTANCE.i(TAG, String.format("录音完成！ path: %s ； 大小：%s", resultFile.getAbsoluteFile(), resultFile.length() ));
    }

    /**
     * 添加Wav头文件
     */
    private void makeWav() {
        if (!FileUtils.isFile(resultFile) || resultFile.length() == 0) {
            return;
        }
        byte[] header = WavUtils.generateWavFileHeader((int) resultFile.length(), currentConfig.getSampleRate(), currentConfig.getChannelCount(), currentConfig.getEncoding());
        WavUtils.writeHeader(resultFile, header);
    }

    /**
     * 合并文件
     */
    private void mergePcmFile() {
        boolean mergeSuccess = mergePcmFiles(resultFile, files);
        if (!mergeSuccess) {
            notifyError("合并失败");
        }
    }

    /**
     * 合并Pcm文件
     *
     * @param recordFile 输出文件
     * @param files      多个文件源
     * @return 是否成功
     */
    private boolean mergePcmFiles(File recordFile, List<File> files) {
        if (recordFile == null || files == null || files.size() <= 0) {
            return false;
        }

        FileOutputStream fos = null;
        BufferedOutputStream outputStream = null;
        byte[] buffer = new byte[1024];
        try {
            fos = new FileOutputStream(recordFile);
            outputStream = new BufferedOutputStream(fos);

            for (int i = 0; i < files.size(); i++) {
                BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(files.get(i)));
                int readCount;
                while ((readCount = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, readCount);
                }
                inputStream.close();
            }
        } catch (Exception e) {
            Logger.INSTANCE.e( TAG, e.getMessage());
            return false;
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < files.size(); i++) {
            files.get(i).delete();
        }
        files.clear();
        return true;
    }

    /**
     * 根据当前的时间生成相应的文件名
     * 实例 record_20160101_13_15_12
     */
    private String getTempFilePath() {
        String fileDir = String.format(Locale.getDefault(), "%s/Record/", Environment.getExternalStorageDirectory().getAbsolutePath());
        if (!FileUtils.createOrExistsDir(fileDir)) {
            Logger.INSTANCE.e(TAG, "文件夹创建失败："+ fileDir);
        }
        String fileName = String.format(Locale.getDefault(), "record_tmp_%s", FileUtils.getNowString(new SimpleDateFormat("yyyyMMdd_HH_mm_ss", Locale.SIMPLIFIED_CHINESE)));
        return String.format(Locale.getDefault(), "%s%s.pcm", fileDir, fileName);
    }

    /**
     * 表示当前状态
     */
    public enum RecordState {
        IDLE("空闲中"),
        RECORDING("录音中"),
        PAUSE("暂停中"),
        STOP("停止"),
        FINISH("录音结束"),
        ERROR("录音失败");
        public String status;
        RecordState(String status){
            this.status = status;
        }
    }

}
