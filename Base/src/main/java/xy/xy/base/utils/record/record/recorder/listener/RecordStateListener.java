package xy.xy.base.utils.record.record.recorder.listener;


import xy.xy.base.utils.record.record.recorder.RecordHelper;

/**
 * @author zhaolewei on 2018/7/11.
 */
public interface RecordStateListener {

    void onStateChange(RecordHelper.RecordState state);
}
