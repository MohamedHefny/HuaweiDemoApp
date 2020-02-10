package com.mohamedhefny.huawei.ui.signin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.huawei.hmf.tasks.Task
import com.huawei.hms.support.hwid.HuaweiIdAuthManager
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper
import com.huawei.hms.support.hwid.result.AuthHuaweiId
import com.huawei.hms.support.hwid.service.HuaweiIdAuthService
import com.mohamedhefny.huawei.R
import com.mohamedhefny.huawei.ui.home.HomeActivity
import com.mohamedhefny.huawei.utils.hideLoading
import com.mohamedhefny.huawei.utils.showLoading
import kotlinx.android.synthetic.main.activity_signin.*

class SignInActivity : AppCompatActivity() {

    private val huaweiIdAuthParams: HuaweiIdAuthParams by lazy {
        HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
            .setIdToken().createParams()
    }

    private val huaweiIdAuthService: HuaweiIdAuthService by
    lazy { HuaweiIdAuthManager.getService(this, huaweiIdAuthParams) }

    private val HUAWEI_AUTH_RQ = 101
    private val TAG: String = SignInActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        huawei_auth_btn.setOnClickListener {
            showLoading()
            startActivityForResult(huaweiIdAuthService.signInIntent, HUAWEI_AUTH_RQ)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        hideLoading()
        if (requestCode == HUAWEI_AUTH_RQ) {
            val authHuaweiIdTask: Task<AuthHuaweiId> =
                HuaweiIdAuthManager.parseAuthResultFromIntent(data)
            if (authHuaweiIdTask.isSuccessful) {//login success
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            } else {//login failed
                Log.d(TAG, "SignIn Failed")
            }
        }
    }
}
