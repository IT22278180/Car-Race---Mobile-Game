import android.content.Context
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import android.view.View
import com.example.carracegame.GameTask
import com.example.carracegame.R

class GameView(var c: Context, var gameTask: GameTask) : View(c) {
    private var myPaint: Paint? = null
    private var speed = 1
    private var time = 0
    private var score = 0
    private var highScore = 0
    private var myPosition = 0
    private val other = ArrayList<HashMap<String, Any>>()
    private val sharedPreferences: SharedPreferences = c.getSharedPreferences("HighScore", Context.MODE_PRIVATE)

    var viewWidth = 0
    var viewHeight = 0

    init {
        myPaint = Paint()
        // Load high score from SharedPreferences
        highScore = sharedPreferences.getInt("highScore", 0)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        viewWidth = this.measuredWidth
        viewHeight = this.measuredHeight

        if (time % 700 < 10 + speed) {
            val map = HashMap<String, Any>()
            map["lane"] = (0..2).random()
            map["startTime"] = time
            other.add(map)
        }
        time = time + 10 + speed
        val carWith = viewWidth / 5
        val carHeight = carWith + 10
        myPaint!!.style = Paint.Style.FILL

        // Draw player's car
        val d = resources.getDrawable(R.drawable.bike, null)
        d.setBounds(
            myPosition * viewWidth / 3 + viewWidth / 15 + 25,
            viewHeight - 2 - carHeight,
            myPosition * viewWidth / 3 + viewWidth / 15 + carWith - 25,
            viewHeight - 2
        )
        d.draw(canvas!!)

        // Draw other cars
        myPaint!!.color = Color.GREEN
        for (i in other.indices) {
            try {
                val carX = other[i]["lane"] as Int * viewWidth / 3 + viewWidth / 15
                var carY = time - other[i]["startTime"] as Int
                val d2 = resources.getDrawable(R.drawable.lorry, null)

                d2.setBounds(
                    carX + 25, carY - carHeight, carX + carWith - 25, carY
                )
                d2.draw(canvas)
                if (other[i]["lane"] as Int == myPosition) {
                    if (carY > viewHeight - 2 - carHeight && carY < viewHeight - 2) {
                        gameTask.closeGame(score)
                    }
                }
                if (carY > viewHeight + carHeight) {
                    other.removeAt(i)
                    score++
                    speed = 1 + Math.abs(score / 8)
                    if (score > highScore) {
                        highScore = score
                        // Save new high score to SharedPreferences
                        val editor = sharedPreferences.edit()
                        editor.putInt("highScore", highScore)
                        editor.apply()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Draw score and speed
        myPaint!!.color = Color.WHITE
        myPaint!!.textSize = 40f
        canvas.drawText("Score: $score", 80f, 80f, myPaint!!)
        canvas.drawText("Speed: $speed", 380f, 80f, myPaint!!)
        canvas.drawText("High Score: $highScore", 80f, 140f, myPaint!!)
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                val x1 = event.x
                if (x1 < viewWidth / 2) {
                    if (myPosition > 0) {
                        myPosition--
                    }
                }
                if (x1 > viewWidth / 2) {
                    if (myPosition < 2) {
                        myPosition++
                    }
                }
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
            }
        }
        return true
    }


}
