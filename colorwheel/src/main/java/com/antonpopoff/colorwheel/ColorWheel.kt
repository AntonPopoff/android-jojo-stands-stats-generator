package com.antonpopoff.colorwheel

import android.content.Context
import android.graphics.*
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.*

class ColorWheel(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : View(context, attrs, defStyleAttr) {

    private var sizeChangeHandled = false
    private var sweepGradient: SweepGradient? = null
    private var radialGradient: RadialGradient? = null

    private val wheelCenter = PointF()
    private var wheelRadius = 0f

    private val thumbPoint = PointF()
    private val thumbRect = Rect()
    private val thumbDrawable = createThumbDrawable()

    private val hsvColor = HSVColor()
    private var currentColor = 0

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        isDither = true
    }

    var thumbRadius = 0f
        set(value) {
            field = value
            setupThumbDrawableInsets()
            invalidate()
        }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.style.ColorWheelDefaultStyle)

    constructor(context: Context) : this(context, null)

    init {
        parseAttributes(context, attrs)
        setupThumbDrawableInsets()
    }

    private fun parseAttributes(context: Context, attrs: AttributeSet?) {
        context.obtainStyledAttributes(attrs, R.styleable.ColorWheel, 0, R.style.ColorWheelDefaultStyle).apply {
            thumbRadius = getDimension(R.styleable.ColorWheel_cw_thumbRadius, 0f)
            recycle()
        }
    }

    private fun createThumbDrawable(): LayerDrawable {
        val thumbDrawable = ShapeDrawable(OvalShape()).apply { paint.color = Color.WHITE }
        val shadowDrawable = ShapeDrawable(OvalShape()).apply { paint.color = Color.GRAY }
        val colorDrawable = ShapeDrawable(OvalShape())
        return LayerDrawable(arrayOf(shadowDrawable, thumbDrawable, colorDrawable))
    }

    private fun setupThumbDrawableInsets() {
        val shadowHInset = (thumbRadius * 0.1f).toInt()
        val shadowVInset = (thumbRadius * 0.1f).toInt()

        val colorHInset = (thumbRadius * 0.25f).toInt()
        val colorVInset = (thumbRadius * 0.25f).toInt()

        thumbDrawable.apply {
            setLayerInset(0, shadowHInset, shadowVInset, -shadowHInset, -shadowVInset)
            setLayerInset(2, colorHInset, colorVInset, colorHInset, colorVInset)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        sizeChangeHandled = false
    }

    override fun onDraw(canvas: Canvas) {
        calculateWheelProperties()
        ensureThumbInitialized()
        updateWheelOnSizeChange()
        calculateThumbRect()

        drawWheelCircleWithShader(canvas, sweepGradient)
        drawWheelCircleWithShader(canvas, radialGradient)
        drawThumb(canvas)
    }

    private fun calculateWheelProperties() {
        val hSpace = (width - paddingLeft - paddingRight) / 2f
        val vSpace = (height - paddingTop - paddingBottom) / 2f

        wheelRadius = min(hSpace, vSpace)

        wheelCenter.apply {
            x = paddingLeft + hSpace
            y = paddingTop + vSpace
        }
    }

    private fun ensureThumbInitialized() {
        if (thumbPoint.x == 0f && thumbPoint.y == 0f) {
            thumbPoint.set(wheelCenter)
        }
    }

    private fun updateWheelOnSizeChange() {
        if (!sizeChangeHandled) {
            updateShader()
            adjustThumbPosition()
            sizeChangeHandled = true
        }
    }

    private fun updateShader() {
        sweepGradient = SweepGradient(wheelCenter.x, wheelCenter.y, hueColors, null)
        radialGradient = RadialGradient(wheelCenter.x, wheelCenter.y, wheelRadius, saturationColors, null, Shader.TileMode.CLAMP)
    }

    private fun adjustThumbPosition() {
        hsvColor.set(currentColor)

        val r = hsvColor.saturation * wheelRadius
        val hueRadians = toRadians(hsvColor.hue)

        thumbPoint.apply {
            x = cos(hueRadians) * r + wheelCenter.x
            y = sin(hueRadians) * r + wheelCenter.y
        }
    }

    private fun calculateCurrentARGBColor() {
        val normalizedX = thumbPoint.x - wheelCenter.x
        val normalizedY = thumbPoint.y - wheelCenter.y
        val hue = (toDegrees(atan2(normalizedY, normalizedX)) + 360) % 360
        val legX = thumbPoint.x - wheelCenter.x
        val legY = thumbPoint.y - wheelCenter.y
        val saturation = sqrt(legX * legX + legY * legY) / wheelRadius

        currentColor = hsvColor.run {
            set(hue, saturation, 1f)
            toARGB()
        }
    }

    private fun calculateThumbRect() {
        thumbRect.set(
                (thumbPoint.x - thumbRadius).toInt(),
                (thumbPoint.y - thumbRadius).toInt(),
                (thumbPoint.x + thumbRadius).toInt(),
                (thumbPoint.y + thumbRadius).toInt()
        )
    }

    private fun drawWheelCircleWithShader(canvas: Canvas, shader: Shader?) {
        paint.shader = shader
        canvas.drawCircle(wheelCenter.x, wheelCenter.y, wheelRadius, paint)
    }

    private fun drawThumb(canvas: Canvas) {
        setColorDrawableColor(currentColor)

        thumbDrawable.apply {
            bounds = thumbRect
            draw(canvas)
        }
    }

    private fun setColorDrawableColor(color: Int) {
        (thumbDrawable.getDrawable(2) as ShapeDrawable).paint.color = color
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                setThumbPositionOnMotionEvent(event)
                calculateCurrentARGBColor()
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                setThumbPositionOnMotionEvent(event)
                calculateCurrentARGBColor()
            }
        }

        return super.onTouchEvent(event)
    }

    private fun setThumbPositionOnMotionEvent(event: MotionEvent) {
        if (isPointWithinCircle(event.x, event.y, wheelCenter.x, wheelCenter.y, wheelRadius)) {
            thumbPoint.set(event)
        } else {
            setEdgePosition(event)
        }

        invalidate()
    }

    private fun setEdgePosition(event: MotionEvent) {
        val normalizedX = event.x - wheelCenter.x
        val normalizedY = event.y - wheelCenter.y
        val angle = atan2(normalizedY, normalizedX)

        thumbPoint.apply {
            x = cos(angle) * wheelRadius + wheelCenter.x
            y = sin(angle) * wheelRadius + wheelCenter.y
        }
    }

    private fun isPointWithinCircle(x: Float, y: Float, cx: Float, cy: Float, radius: Float): Boolean {
        val dx = x - cx
        val dy = y - cy
        return dx * dx + dy * dy <= radius * radius
    }

    companion object {

        private val saturationColors = intArrayOf(Color.WHITE, Color.TRANSPARENT)

        private val hueColors = intArrayOf(
                -0x00010000, -0x0000FE00, -0x0000FC00, -0x0000FA00, -0x0000F800, -0x0000F500,
                -0x0000F100, -0x0000EF00, -0x0000ED00, -0x0000EB00, -0x0000E900, -0x0000E700,
                -0x0000E200, -0x0000E000, -0x0000DE00, -0x0000DC00, -0x0000DA00, -0x0000D800,
                -0x0000D300, -0x0000D100, -0x0000CF00, -0x0000CD00, -0x0000CB00, -0x0000C900,
                -0x0000C400, -0x0000C200, -0x0000C000, -0x0000BE00, -0x0000BC00, -0x0000BA00,
                -0x0000B600, -0x0000B300, -0x0000B100, -0x0000AF00, -0x0000AD00, -0x0000AB00,
                -0x0000A700, -0x0000A500, -0x0000A200, -0x0000A000, -0x00009E00, -0x00009C00,
                -0x00009800, -0x00009600, -0x00009400, -0x00009200, -0x00008F00, -0x00008D00,
                -0x00008900, -0x00008700, -0x00008500, -0x00008300, -0x00008000, -0x00007E00,
                -0x00007A00, -0x00007800, -0x00007600, -0x00007400, -0x00007200, -0x00007000,
                -0x00006B00, -0x00006900, -0x00006700, -0x00006500, -0x00006300, -0x00006100,
                -0x00005C00, -0x00005A00, -0x00005800, -0x00005600, -0x00005400, -0x00005200,
                -0x00004E00, -0x00004B00, -0x00004900, -0x00004700, -0x00004500, -0x00004300,
                -0x00003F00, -0x00003D00, -0x00003A00, -0x00003800, -0x00003600, -0x00003400,
                -0x00003000, -0x00002E00, -0x00002C00, -0x00002900, -0x00002700, -0x00002500,
                -0x00002100, -0x00001F00, -0x00001D00, -0x00001B00, -0x00001800, -0x00001600,
                -0x00001200, -0x00001000, -0x00000E00, -0x00000C00, -0x00000900, -0x00000700,
                -0x00000300, -0x00000100, -0x00020100, -0x00040100, -0x00060100, -0x00080100,
                -0x000D0100, -0x000F0100, -0x00110100, -0x00130100, -0x00150100, -0x00170100,
                -0x001C0100, -0x001E0100, -0x00200100, -0x00220100, -0x00240100, -0x00260100,
                -0x002B0100, -0x002D0100, -0x002F0100, -0x00310100, -0x00330100, -0x00350100,
                -0x00390100, -0x003C0100, -0x003E0100, -0x00400100, -0x00420100, -0x00440100,
                -0x00480100, -0x004A0100, -0x004D0100, -0x004F0100, -0x00510100, -0x00530100,
                -0x00570100, -0x00590100, -0x005B0100, -0x005D0100, -0x00600100, -0x00620100,
                -0x00660100, -0x00680100, -0x006A0100, -0x006C0100, -0x006F0100, -0x00710100,
                -0x00750100, -0x00770100, -0x00790100, -0x007B0100, -0x007D0100, -0x007F0100,
                -0x00840100, -0x00860100, -0x00880100, -0x008A0100, -0x008C0100, -0x008E0100,
                -0x00930100, -0x00950100, -0x00970100, -0x00990100, -0x009B0100, -0x009D0100,
                -0x00A10100, -0x00A40100, -0x00A60100, -0x00A80100, -0x00AA0100, -0x00AC0100,
                -0x00B00100, -0x00B20100, -0x00B50100, -0x00B70100, -0x00B90100, -0x00BB0100,
                -0x00BF0100, -0x00C10100, -0x00C30100, -0x00C60100, -0x00C80100, -0x00CA0100,
                -0x00CE0100, -0x00D00100, -0x00D20100, -0x00D50100, -0x00D70100, -0x00D90100,
                -0x00DD0100, -0x00DF0100, -0x00E10100, -0x00E30100, -0x00E50100, -0x00E80100,
                -0x00EC0100, -0x00EE0100, -0x00F00100, -0x00F20100, -0x00F40100, -0x00F70100,
                -0x00FB0100, -0x00FD0100, -0x00FF0100, -0x00FF00FE, -0x00FF00FC, -0x00FF00FA,
                -0x00FF00F5, -0x00FF00F3, -0x00FF00F1, -0x00FF00EF, -0x00FF00ED, -0x00FF00EB,
                -0x00FF00E7, -0x00FF00E4, -0x00FF00E2, -0x00FF00E0, -0x00FF00DE, -0x00FF00DC,
                -0x00FF00D8, -0x00FF00D6, -0x00FF00D3, -0x00FF00D1, -0x00FF00CF, -0x00FF00CD,
                -0x00FF00C9, -0x00FF00C7, -0x00FF00C4, -0x00FF00C2, -0x00FF00C0, -0x00FF00BE,
                -0x00FF00BA, -0x00FF00B8, -0x00FF00B6, -0x00FF00B3, -0x00FF00B1, -0x00FF00AF,
                -0x00FF00AB, -0x00FF00A9, -0x00FF00A7, -0x00FF00A5, -0x00FF00A2, -0x00FF00A0,
                -0x00FF009C, -0x00FF009A, -0x00FF0098, -0x00FF0096, -0x00FF0094, -0x00FF0091,
                -0x00FF008D, -0x00FF008B, -0x00FF0089, -0x00FF0087, -0x00FF0085, -0x00FF0083,
                -0x00FF007E, -0x00FF007C, -0x00FF007A, -0x00FF0078, -0x00FF0076, -0x00FF0074,
                -0x00FF0070, -0x00FF006D, -0x00FF006B, -0x00FF0069, -0x00FF0067, -0x00FF0065,
                -0x00FF0061, -0x00FF005E, -0x00FF005C, -0x00FF005A, -0x00FF0058, -0x00FF0056,
                -0x00FF0052, -0x00FF0050, -0x00FF004D, -0x00FF004B, -0x00FF0049, -0x00FF0047,
                -0x00FF0043, -0x00FF0041, -0x00FF003F, -0x00FF003D, -0x00FF003A, -0x00FF0038,
                -0x00FF0034, -0x00FF0032, -0x00FF0030, -0x00FF002E, -0x00FF002C, -0x00FF0029,
                -0x00FF0025, -0x00FF0023, -0x00FF0021, -0x00FF001F, -0x00FF001D, -0x00FF001B,
                -0x00FF0016, -0x00FF0014, -0x00FF0012, -0x00FF0010, -0x00FF000E, -0x00FF000C,
                -0x00FF0007, -0x00FF0005, -0x00FF0003, -0x00FF0001, -0x00FF0201, -0x00FF0401,
                -0x00FF0801, -0x00FF0B01, -0x00FF0D01, -0x00FF0F01, -0x00FF1101, -0x00FF1301,
                -0x00FF1701, -0x00FF1A01, -0x00FF1C01, -0x00FF1E01, -0x00FF2001, -0x00FF2201,
                -0x00FF2601, -0x00FF2801, -0x00FF2B01, -0x00FF2D01, -0x00FF2F01, -0x00FF3101,
                -0x00FF3501, -0x00FF3701, -0x00FF3901, -0x00FF3C01, -0x00FF3E01, -0x00FF4001,
                -0x00FF4401, -0x00FF4601, -0x00FF4801, -0x00FF4A01, -0x00FF4D01, -0x00FF4F01,
                -0x00FF5301, -0x00FF5501, -0x00FF5701, -0x00FF5901, -0x00FF5B01, -0x00FF5D01,
                -0x00FF6201, -0x00FF6401, -0x00FF6601, -0x00FF6801, -0x00FF6A01, -0x00FF6C01,
                -0x00FF7101, -0x00FF7301, -0x00FF7501, -0x00FF7701, -0x00FF7901, -0x00FF7B01,
                -0x00FF7F01, -0x00FF8201, -0x00FF8401, -0x00FF8601, -0x00FF8801, -0x00FF8A01,
                -0x00FF8E01, -0x00FF9001, -0x00FF9301, -0x00FF9501, -0x00FF9701, -0x00FF9901,
                -0x00FF9D01, -0x00FF9F01, -0x00FFA101, -0x00FFA401, -0x00FFA601, -0x00FFA801,
                -0x00FFAC01, -0x00FFAE01, -0x00FFB001, -0x00FFB301, -0x00FFB501, -0x00FFB701,
                -0x00FFBB01, -0x00FFBD01, -0x00FFBF01, -0x00FFC101, -0x00FFC301, -0x00FFC601,
                -0x00FFCA01, -0x00FFCC01, -0x00FFCE01, -0x00FFD001, -0x00FFD201, -0x00FFD401,
                -0x00FFD901, -0x00FFDB01, -0x00FFDD01, -0x00FFDF01, -0x00FFE101, -0x00FFE301,
                -0x00FFE801, -0x00FFEA01, -0x00FFEC01, -0x00FFEE01, -0x00FFF001, -0x00FFF201,
                -0x00FFF701, -0x00FFF901, -0x00FFFB01, -0x00FFFD01, -0x00FFFF01, -0x00FDFF01,
                -0x00F9FF01, -0x00F7FF01, -0x00F4FF01, -0x00F2FF01, -0x00F0FF01, -0x00EEFF01,
                -0x00EAFF01, -0x00E8FF01, -0x00E6FF01, -0x00E3FF01, -0x00E1FF01, -0x00DFFF01,
                -0x00DBFF01, -0x00D9FF01, -0x00D7FF01, -0x00D5FF01, -0x00D2FF01, -0x00D0FF01,
                -0x00CCFF01, -0x00CAFF01, -0x00C8FF01, -0x00C6FF01, -0x00C3FF01, -0x00C1FF01,
                -0x00BDFF01, -0x00BBFF01, -0x00B9FF01, -0x00B7FF01, -0x00B5FF01, -0x00B3FF01,
                -0x00AEFF01, -0x00ACFF01, -0x00AAFF01, -0x00A8FF01, -0x00A6FF01, -0x00A4FF01,
                -0x009FFF01, -0x009DFF01, -0x009BFF01, -0x0099FF01, -0x0097FF01, -0x0095FF01,
                -0x0090FF01, -0x008EFF01, -0x008CFF01, -0x008AFF01, -0x0088FF01, -0x0086FF01,
                -0x0082FF01, -0x007FFF01, -0x007DFF01, -0x007BFF01, -0x0079FF01, -0x0077FF01,
                -0x0073FF01, -0x0071FF01, -0x006FFF01, -0x006CFF01, -0x006AFF01, -0x0068FF01,
                -0x0064FF01, -0x0062FF01, -0x0060FF01, -0x005DFF01, -0x005BFF01, -0x0059FF01,
                -0x0055FF01, -0x0053FF01, -0x0051FF01, -0x004FFF01, -0x004CFF01, -0x004AFF01,
                -0x0046FF01, -0x0044FF01, -0x0042FF01, -0x0040FF01, -0x003EFF01, -0x003CFF01,
                -0x0037FF01, -0x0035FF01, -0x0033FF01, -0x0031FF01, -0x002FFF01, -0x002DFF01,
                -0x0028FF01, -0x0026FF01, -0x0024FF01, -0x0022FF01, -0x0020FF01, -0x001EFF01,
                -0x0019FF01, -0x0017FF01, -0x0015FF01, -0x0013FF01, -0x0011FF01, -0x000FFF01,
                -0x000BFF01, -0x0008FF01, -0x0006FF01, -0x0004FF01, -0x0002FF01, -0x0000FF01,
                -0x0000FF05, -0x0000FF07, -0x0000FF09, -0x0000FF0C, -0x0000FF0E, -0x0000FF10,
                -0x0000FF14, -0x0000FF16, -0x0000FF18, -0x0000FF1A, -0x0000FF1D, -0x0000FF1F,
                -0x0000FF23, -0x0000FF25, -0x0000FF27, -0x0000FF29, -0x0000FF2C, -0x0000FF2E,
                -0x0000FF32, -0x0000FF34, -0x0000FF36, -0x0000FF38, -0x0000FF3A, -0x0000FF3D,
                -0x0000FF41, -0x0000FF43, -0x0000FF45, -0x0000FF47, -0x0000FF49, -0x0000FF4B,
                -0x0000FF50, -0x0000FF52, -0x0000FF54, -0x0000FF56, -0x0000FF58, -0x0000FF5A,
                -0x0000FF5F, -0x0000FF61, -0x0000FF63, -0x0000FF65, -0x0000FF67, -0x0000FF69,
                -0x0000FF6D, -0x0000FF70, -0x0000FF72, -0x0000FF74, -0x0000FF76, -0x0000FF78,
                -0x0000FF7C, -0x0000FF7E, -0x0000FF80, -0x0000FF83, -0x0000FF85, -0x0000FF87,
                -0x0000FF8B, -0x0000FF8D, -0x0000FF8F, -0x0000FF91, -0x0000FF94, -0x0000FF96,
                -0x0000FF9A, -0x0000FF9C, -0x0000FF9E, -0x0000FFA0, -0x0000FFA2, -0x0000FFA5,
                -0x0000FFA9, -0x0000FFAB, -0x0000FFAD, -0x0000FFAF, -0x0000FFB1, -0x0000FFB3,
                -0x0000FFB8, -0x0000FFBA, -0x0000FFBC, -0x0000FFBE, -0x0000FFC0, -0x0000FFC2,
                -0x0000FFC7, -0x0000FFC9, -0x0000FFCB, -0x0000FFCD, -0x0000FFCF, -0x0000FFD1,
                -0x0000FFD5, -0x0000FFD8, -0x0000FFDA, -0x0000FFDC, -0x0000FFDE, -0x0000FFE0,
                -0x0000FFE4, -0x0000FFE6, -0x0000FFE9, -0x0000FFEB, -0x0000FFED, -0x0000FFEF
        )
    }
}