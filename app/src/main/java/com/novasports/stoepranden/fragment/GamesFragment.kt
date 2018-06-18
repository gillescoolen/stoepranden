package com.novasports.stoepranden.fragment

import android.content.Context
import android.content.Intent
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.novasports.stoepranden.R
import kotlinx.android.synthetic.main.fragment_games.*
import org.jetbrains.anko.support.v4.toast
import java.util.*
import java.util.concurrent.TimeUnit
import android.os.*
import android.media.RingtoneManager
import org.jetbrains.anko.support.v4.indeterminateProgressDialog


class GamesFragment : Fragment() {

    var Point = 0
    var Point2 = 0

    var uid:String = ""
    var winner:String = ""

    private var firebaseFirestore: FirebaseFirestore? = null

    val PLAYER_KEY = "uid"
    val SCORE_KEY = "score"
    val WINNER_KEY = "result"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_games, container, false)
    }

    override fun onStart() {
        super.onStart()
        if (this@GamesFragment.isVisible) {
            timer()
            firebaseFirestore = FirebaseFirestore.getInstance()

            btnShare.setOnClickListener(View.OnClickListener { share() })
            btnPlayer.setOnClickListener(View.OnClickListener { choosePlayer() })
            btnSaveGame.setOnClickListener(View.OnClickListener { saveGame() })
        }
    }

    private fun choosePlayer() {
        val rnd = Random()
        var start = ""

        val n = rnd.nextInt(2)

        when (n) {
            0 -> start = "Jij begint"
            1 -> start = "Tegenstander begint"
        }
        tvPlayer.setText(start)
        btnPlayer.visibility = View.GONE
        btnPlayer.isEnabled = false

        btnTimeStart.visibility = View.VISIBLE
        btnTimeStart.isEnabled = true

    }


    private fun timer() {
        btnTimeStart.setOnClickListener(View.OnClickListener {
            btnTimeStart.isEnabled = false
            btnTimeStart.setText("HET SPEL IS BEZIG!")

            object : CountDownTimer(5000, 1000) { // adjust the milli seconds here

                override fun onTick(millisUntilFinished: Long) {
                    tvTime.setText("" + String.format("%d min - %d sec",
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))))
                }

                override fun onFinish() {
                    tvTime.setText("Het spel is af!")

                    tvScoreText.visibility = View.VISIBLE
                    scoreLayout.visibility = View.VISIBLE

                    tvPlayer.visibility = View.INVISIBLE

                    btnTimeStart.visibility = View.GONE
                    btnTimeStart.isEnabled = false

                    btnSaveGame.visibility = View.VISIBLE
                    btnSaveGame.isEnabled = true

                    btnShare.visibility = View.VISIBLE
                    btnShare.isEnabled = true

                    val vibratorService = context!!.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                    var r = RingtoneManager.getRingtone(context, notification)

                    r.play()

                    if (Build.VERSION.SDK_INT >= 26)
                        vibratorService.vibrate(VibrationEffect.createOneShot(1000,20))
                     else
                        vibratorService.vibrate(1000)
                }
            }.start()
        })
    }

    private fun savePoints() {
        Point = Integer.parseInt(tvScore.getText().toString())
        Point2 = Integer.parseInt(tvScore2.getText().toString())
    }

    private fun share() {
        savePoints()
        val message: String

        if (Point > Point2) {
            message = "Ik heb zojuist Stoepranden gewonnen met een score van: " + Point.toString() + " - " + Point2.toString() + " !"
        } else if (Point < Point2) {
            message = "Ik heb zojuist Stoepranden verloren met een score van: " + Point.toString() + " - " + Point2.toString() + " !"
        } else {
            message = "Ik heb zojuist Stoepranden gelijk gespeeld met een score van: " + Point.toString() + " - " + Point2.toString() + " !"
        }

        val share = Intent(Intent.ACTION_SEND)
        share.type = "text/plain"
        share.putExtra(Intent.EXTRA_TEXT, message)

        startActivity(Intent.createChooser(share, "Open dialog"))
        btnShare.setText("JE UITSLAG IS GEDEELD!")
    }

    private fun saveGame() {
        btnSaveGame.isEnabled = false
        var dialog = indeterminateProgressDialog("Bezig met opslaan...")
        dialog.show()
        val handler = Handler()
        handler.postDelayed(Runnable { dialog.dismiss() }, 1000)
        savePoints()

        if (Point > Point2) {
            winner = "Win"
        } else if (Point < Point2) {
            winner = "Loss"
        } else {
            winner = "Draw"
        }
        saveGameInformation(winner)
    }

    private fun saveGameInformation(winner: String) {
        var firebaseAuth: FirebaseAuth? = null

        firebaseAuth = FirebaseAuth.getInstance()
        val user = firebaseAuth.getCurrentUser()

        uid = user!!.getUid()

        val scoreTS = tvScore.getText().toString().trim({ it <= ' ' }) + " - " + tvScore2.getText().toString().trim({ it <= ' ' })
        val userId = user!!.getUid()

        val userMap = HashMap<String, Any>()
        userMap[PLAYER_KEY] = userId
        userMap[SCORE_KEY] = scoreTS
        userMap[WINNER_KEY] = winner

        firebaseFirestore!!.collection("games").add(userMap).addOnCompleteListener { btnSaveGame.setText("JE SPEL IS OPGESLAGEN!") }
    }
}