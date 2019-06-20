package csedevelopers.freaky.developers.womensafety

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.gson.JsonObject
import com.mobsandgeeks.saripaar.ValidationError
import com.mobsandgeeks.saripaar.Validator
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword
import com.mobsandgeeks.saripaar.annotation.NotEmpty
import com.mobsandgeeks.saripaar.annotation.Password

/**
 * Created by PURUSHOTAM on 8/25/2017.
 */
class Signup : AppCompatActivity(), Validator.ValidationListener {

    internal lateinit var signIn: ImageView
    @NotEmpty
    internal lateinit var firstName: EditText

    @NotEmpty
    internal lateinit var lastName: EditText

    @NotEmpty
    internal lateinit var email: EditText

    @NotEmpty
    @Password(min = 6)
    internal lateinit var password: EditText

    @ConfirmPassword
    @NotEmpty
    internal lateinit var confirmPassword: EditText

    @NotEmpty
    internal lateinit var phone: TextView

    internal lateinit var alreadyReg: TextView


    private var validator: Validator? = null
    private var context: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        signIn = findViewById(R.id.signin) as ImageView
        firstName = findViewById(R.id.firstName) as EditText
        lastName = findViewById(R.id.lastName) as EditText
        email = findViewById(R.id.Email) as EditText
        password = findViewById(R.id.passWord) as EditText
        confirmPassword = findViewById(R.id.confirmPassword) as EditText
        phone = findViewById(R.id.phonee) as EditText
        alreadyReg = findViewById(R.id.alreadyreg) as TextView

        validator = Validator(this)
        validator!!.setValidationListener(this)
        context = this

    }

    override fun onStart() {
        super.onStart()
        signIn.setOnClickListener { validator!!.validate() }
        alreadyReg.setOnClickListener { finish() }
    }

    override fun onValidationSucceeded() {

        val jsonObject = JsonObject()
        try {
            jsonObject.addProperty("fName", firstName.text.toString())
            jsonObject.addProperty("lName", lastName.text.toString())
            jsonObject.addProperty("email", email.text.toString())
            jsonObject.addProperty("pass", password.text.toString())
            jsonObject.addProperty("phone", phone.text.toString())
            jsonObject.addProperty("fcmId", fcmId)


            val intent = Intent(this@Signup, OTPVeification::class.java)
            intent.putExtra("jsonObject", jsonObject.toString())
            intent.putExtra("phoneNo", phone.text.toString())
            startActivity(intent)

        } catch (e: Exception) {

            e.printStackTrace()

        }


    }

    override fun onValidationFailed(errors: List<ValidationError>) {

        for (error in errors) {
            val view = error.view
            val message = error.getCollatedErrorMessage(this)

            // Display error messages ;)
            if (view is EditText) {
                view.error = message
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        }


    }


}
