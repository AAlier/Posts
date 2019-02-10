package mobile.samplelistload.ui

import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.widget.Toast

open class BaseActivity: AppCompatActivity(){

    fun shortToast(message: String){
        showToast(message, Toast.LENGTH_SHORT)
    }

    fun longToast(message: String){
        showToast(message, Toast.LENGTH_LONG)
    }

    fun showToast(message: String, durationType: Int){
        runOnUiThread {
            val toast = Toast.makeText(this, message, durationType)
            toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 100)
            toast.show()
        }
    }
}