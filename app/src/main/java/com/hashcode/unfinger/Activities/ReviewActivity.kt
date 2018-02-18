package com.hashcode.unfinger.Activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.EditText
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.hashcode.unfinger.R
import com.hashcode.unfinger.models.Review

class ReviewActivity : AppCompatActivity() {

    lateinit var reviewEditText : EditText
    lateinit var nameEditText : EditText
    lateinit var mFirebaseDatabase : DatabaseReference
    var REVIEWS_KEY = "reviews"
    val REVIEW_STORE = "review store"
    val NAME_STORE = "username"
    lateinit var progressBar : ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)
        val toolbar : Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.title = "Submit A Review"
        mFirebaseDatabase = FirebaseDatabase.getInstance().reference
        val fab : FloatingActionButton = findViewById(R.id.send_review_button)
        reviewEditText = findViewById(R.id.review_edit_text)
        nameEditText = findViewById(R.id.reviewer_name_edit_text)
        if (savedInstanceState != null){
            nameEditText.setText(savedInstanceState.getString(NAME_STORE))
            reviewEditText.setText(savedInstanceState.getString(REVIEW_STORE))
        }
        var review : Review
        fab.setOnClickListener { view ->
            if(isOnline(this)){
                if(reviewValid() && nameValid()){
                    progressBar.show()
                    review = Review(nameEditText.text.toString(),reviewEditText.text.toString())
                    mFirebaseDatabase.child(REVIEWS_KEY).push().setValue(review, {
                        databaseError: DatabaseError?, databaseReference: DatabaseReference? ->
                        if (databaseError == null){
                            progressBar.cancel()
                            Snackbar.make(view, "Thank you for your review", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show()
                        }
                    })
                }
            }
            else{
                Snackbar.make(view, "No Internet Connection", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
            }
        }
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        progressBar = ProgressDialog(this)
        progressBar.setMessage("Submitting Review")
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressBar.isIndeterminate = true
        progressBar.progress = 0
    }

    fun reviewValid() : Boolean{
        var reviewText = reviewEditText.text.toString()
        if(reviewText.length < 3) {
            reviewEditText.error = "too short"
            return false
        }
        else{ return true}
    }
    fun nameValid() : Boolean {
        var name = nameEditText.text.toString()
        if(name.length == 0){
            nameEditText.error = "Enter your name"
            return false
        }
        else{
            return true
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putString(NAME_STORE, nameEditText.text.toString())
        outState?.putString(REVIEW_STORE, reviewEditText.text.toString())
    }

    fun isOnline(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).state == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).state == NetworkInfo.State.CONNECTED
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return super.onOptionsItemSelected(item)
        var id = item!!.itemId
        if(id == R.id.home){
            onBackPressed()
        }
    }

}
