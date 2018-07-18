package com.ubtechinc.alpha.mini.common

/**
 * @Deseription
 * @Author tanghongyu
 * @Time 2018/3/30 11:15
 */

interface IBlocklyControl {

    fun bluetoothManage()
    fun getRobotConnectionState()
    fun playTTS(text: String)
    fun stopTTS()
    fun getActionList()
    fun playAction(actionName: String)
    fun stopAllAction()
    fun faceDetect(timeout: Int)
    fun faceAnalysis(timeout: Int)
    fun objectDetect(content: String)
    fun faceRecognise(timeout: Int)
    fun getExpressionList()
    fun playExpression(expression: String)
    fun controlMouthLamp(isOpen: Boolean)
    fun setMouthLamp(content: String)
    fun takePicture(type: Int)
    fun controlFindFace(content: String)
    fun moveRobot(content: String)
    fun switchTranslateModel(isEnter: Boolean)
    fun controlRegisterFace(content: String)
    fun getRegisterFaces()
    fun controlBehavior(content: String)
    fun loadProductList()
    fun saveProduct(content: String)
    fun deleteProduct(ids: String)
    fun getProductContent(id: String)
    fun exitCodingModel()
    fun checkRunCondition()
    fun revertRobotOrigin()
    fun reportPhoneKeyBack()
    fun unregisterRobotSensor()
    fun registerPhoneSensors()
    fun unregisterPhoneSensor()
    fun registerRobotSensors()
    fun reportRobotBleStatus(status : Boolean)
    fun startPlayVideo(url : String)
    fun startRender(url: String)
    fun startRunProgram()
    fun stopRunProgram()
    fun upload(content: String)
}
