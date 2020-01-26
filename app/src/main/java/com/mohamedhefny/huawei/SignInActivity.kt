package com.mohamedhefny.huawei

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.huawei.hmf.tasks.Task
import com.huawei.hms.support.hwid.HuaweiIdAuthManager
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper
import com.huawei.hms.support.hwid.result.AuthHuaweiId
import com.huawei.hms.support.hwid.service.HuaweiIdAuthService
import kotlinx.android.synthetic.main.activity_main.*

class SignInActivity : AppCompatActivity() {

    private val huaweiIdAuthParams: HuaweiIdAuthParams by lazy {
        HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
            .setIdToken().createParams()
    }

    private val huaweiIdAuthService: HuaweiIdAuthService by
    lazy { HuaweiIdAuthManager.getService(this, huaweiIdAuthParams) }

    private val HUAWEI_AUTH_RQ = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        huawei_auth_btn.setOnClickListener {
            startActivityForResult(huaweiIdAuthService.signInIntent, HUAWEI_AUTH_RQ)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == HUAWEI_AUTH_RQ) {
            val authHuaweiIdTask: Task<AuthHuaweiId> =
                HuaweiIdAuthManager.parseAuthResultFromIntent(data)
            if (authHuaweiIdTask.isSuccessful) {//login success

            } else {//login failed

            }
        }
    }
}
