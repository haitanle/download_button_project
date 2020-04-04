package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import kotlin.properties.Delegates

// @JvmOverloads override all constructor of the view class
class LoadingButton @JvmOverloads constructor(

    // pass in parameter of context, AttributeSet, styleAttr
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0

    // extends the View class
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var widthSize = 10
    private var heightSize = 10

    private val valueAnimator = ValueAnimator()

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->

    }

    init {

        // initialize the inflater
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        inflater.inflate(R.layout.custom_button, this, true)

        // get the layout id
        var textLabel: TextView = findViewById<View>(R.id.labelTextView) as TextView

        // get the reference to styleable
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.LoadingButton, 0, 0)

        try {
            // get and sync the loadingButton text attribute

            var textValue = a.getString(R.styleable.LoadingButton_buttonText)
            var textColor = a.getColor(R.styleable.LoadingButton_textColor, Color.BLACK)

            textLabel.setText(textValue)
            textLabel.setTextColor(textColor)

        }finally{

            //recycle the reference to styleable
            a.recycle()
        }
    }

    // Override on create custom component/viewgroup
//    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
//        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
//        val h: Int = resolveSizeAndState(
//            MeasureSpec.getSize(w),
//            heightMeasureSpec,
//            0
//        )
//        widthSize = w
//        heightSize = h
//        setMeasuredDimension(w, h)
//    }

}