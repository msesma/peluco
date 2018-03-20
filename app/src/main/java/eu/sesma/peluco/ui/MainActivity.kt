package eu.sesma.peluco.ui

import android.Manifest
import android.content.Intent
import android.os.Bundle
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import eu.sesma.peluco.R
import eu.sesma.peluco.bt.BlunoLibrary
import eu.sesma.peluco.bt.ConnectionListener
import eu.sesma.peluco.platform.BaseActivity
import javax.inject.Inject



class MainActivity : BaseActivity(), ConnectionListener {

    @Inject
    lateinit var presenter: MainActivityPresenter
    @Inject
    lateinit var decorator: MainActivityDecorator

    private val permissionListener = object : PermissionListener {
        override fun onPermissionGranted(response: PermissionGrantedResponse) {
        }

        override fun onPermissionDenied(response: PermissionDeniedResponse) {
        }

        override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest, token: PermissionToken) {

        }
    }

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_main)
        activityComponent.inject(this)

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(permissionListener)
                .check()

        decorator.bind(getRootView())
        presenter.initialize(decorator)
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
    }

    override fun onPause() {
        super.onPause()
        presenter.onPause()
    }

    override fun onStop() {
        super.onStop()
        presenter.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        decorator.dispose()
        presenter.dispose()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        presenter.onEnableBtResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onConnectionStateChange(connectionStateEnum: BlunoLibrary.connectionStateEnum) {
        presenter.onConnectionStateChange(connectionStateEnum)
    }

    override fun onSerialReceived(text: String) {
        presenter.onSerialReceived(text)
    }
}
