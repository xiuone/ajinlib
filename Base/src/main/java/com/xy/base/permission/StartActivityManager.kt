package com.xy.base.permission

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import com.xy.base.permission.PermissionUtils.findActivity
import com.xy.base.utils.AndroidVersion.isAndroid13
import com.xy.base.utils.exp.startAppActivity

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/XXPermissions
 * time   : 2023/04/05
 * desc   : startActivity 管理器
 */
internal object StartActivityManager {
   private const val SUB_INTENT_KEY = "sub_intent_key"
   fun getSubIntentInMainIntent(mainIntent: Intent?): Intent? {
      return if (isAndroid13()) mainIntent?.getParcelableExtra(SUB_INTENT_KEY, Intent::class.java)
      else mainIntent?.getParcelableExtra(SUB_INTENT_KEY)
   }

   fun getDeepSubIntent(superIntent: Intent?): Intent? {
      val subIntent = getSubIntentInMainIntent(superIntent)
      return if (subIntent != null) {
         getDeepSubIntent(subIntent)
      } else superIntent
   }

   fun addSubIntentToMainIntent( mainIntent: Intent?, @Nullable subIntent: Intent?, ): Intent? {
      if (mainIntent == null && subIntent != null) {
         return subIntent
      }
      if (subIntent == null) {
         return mainIntent
      }
      val deepSubIntent = getDeepSubIntent(mainIntent)
      deepSubIntent?.putExtra(SUB_INTENT_KEY, subIntent)
      return mainIntent
   }

   fun startActivity(context: Context, intent: Intent?): Boolean = startActivity(StartActivityDelegateContextImpl(context), intent)

   fun startActivity(activity: Activity, intent: Intent?): Boolean {
      return startActivity(StartActivityDelegateActivityImpl(activity), intent)
   }

   fun startActivity(fragment: Fragment, intent: Intent?): Boolean {
      return startActivity(StartActivityDelegateFragmentImpl(fragment), intent)
   }


   fun startActivity(delegate: IStartActivityDelegate, intent: Intent?): Boolean {
      return try {
         delegate.startActivity(intent)
         true
      } catch (e: Exception) {
         e.printStackTrace()
         val subIntent = getSubIntentInMainIntent(intent)
            ?: return false
         startActivity(delegate, subIntent)
      }
   }

   fun startActivityForResult(activity: Activity, intent: Intent, requestCode: Int): Boolean =
      startActivityForResult(StartActivityDelegateActivityImpl(activity), intent, requestCode)

   fun startActivityForResult(fragment: Fragment, intent: Intent?, requestCode: Int): Boolean =
      startActivityForResult(StartActivityDelegateFragmentImpl(fragment), intent, requestCode)

   fun startActivityForResult(delegate: IStartActivityDelegate, intent: Intent?, requestCode: Int): Boolean {
      return try {
         delegate.startActivityForResult(intent, requestCode)
         true
      } catch (e: Exception) {
         e.printStackTrace()
         val subIntent = getSubIntentInMainIntent(intent) ?: return false
         startActivityForResult(delegate, subIntent, requestCode)
      }
   }

   interface IStartActivityDelegate {
      fun startActivity(intent: Intent?)
      fun startActivityForResult(intent: Intent?, requestCode: Int)
   }

   private class StartActivityDelegateContextImpl (private val mContext: Context) : IStartActivityDelegate {
      override fun startActivity(intent: Intent?) {
         if (intent == null)return
         mContext.startAppActivity(intent)
      }

      override fun startActivityForResult(intent: Intent?, requestCode: Int) {
         if (intent == null)return
         val activity = findActivity(mContext)
         if (activity != null ) {
            activity.startActivityForResult(intent, requestCode)
            return
         }
         startActivity(intent)
      }
   }

   private class StartActivityDelegateActivityImpl (private val mActivity: Activity) :
      IStartActivityDelegate {
      override fun startActivity(intent: Intent?) {
         if (intent == null)return
         mActivity.startAppActivity(intent)
      }

      override fun startActivityForResult(intent: Intent?, requestCode: Int) {
         if (intent == null)return
         mActivity.startActivityForResult(intent, requestCode)
      }
   }

   private class StartActivityDelegateFragmentImpl(private val mFragment: Fragment) : IStartActivityDelegate {
      override fun startActivity(intent: Intent?) {
         if (intent == null)return
         mFragment.startActivity(intent)
      }

      override fun startActivityForResult(intent: Intent?, requestCode: Int) {
         if (intent == null)return
         mFragment.startActivityForResult(intent, requestCode)
      }
   }
}