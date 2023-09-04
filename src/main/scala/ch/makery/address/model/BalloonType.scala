package ch.makery.address.model

import scalafx.scene.image.Image

trait BalloonType {
  def imgList: List[Image] = List(
    new Image("images/balloons/Game_RedBalloon_0_0.png"), new Image("images/balloons/Game_BlueBalloon_0_0.png"),
    new Image("images/balloons/Game_GreenBalloon_0_0.png"), new Image("images/balloons/Game_YellowBalloon_0_0.png"),
    new Image("images/balloons/Game_PurpleBalloon_0_0.png")
  )
  //TODO Update images to increase transparent borders
  def speed: Int
  def health: Int
  def width: Int = 64
  def height: Int = 64
}

object RedBalloon extends BalloonType {
  override def imgList: List[Image] = super.imgList.dropRight(4)
  def speed = 64
  def health = 1
}

object BlueBalloon extends BalloonType {
  override def imgList: List[Image] = super.imgList.dropRight(3)
  def speed = 64
  def health = 2
}

object GreenBalloon extends BalloonType {
  override def imgList: List[Image] = super.imgList.dropRight(2)
  def speed = 64
  def health = 3
}

object YellowBalloon extends BalloonType {
  override def imgList: List[Image] = super.imgList.head :: super.imgList.drop(3).dropRight(1)
  def speed = 96
  def health = 2
}

object PurpleBalloon extends BalloonType {
  override def imgList: List[Image] = super.imgList.head :: super.imgList.drop(3)
  def speed = 96
  def health = 3
}
