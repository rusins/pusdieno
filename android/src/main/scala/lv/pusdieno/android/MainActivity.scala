package lv.pusdieno.android

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.graphics.drawable.Animatable
import controllers.routes

class MainActivity extends AppCompatActivity {
  // allows accessing `.value` on TR.resource.constants
  implicit val context: MainActivity = this

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    // type ascription is required due to SCL-10491
    val vh: TypedViewHolder.main = TypedViewHolder.setContentView(this, TR.layout.main).asInstanceOf[TypedViewHolder.main]
    val url = routes.EateriesController.eat().url
    vh.text.setText(s"Hello world, from ${TR.string.app_name.value}\n" +
      s"I believe that the EateriesController eat() url is $url")
    vh.image.getDrawable match {
      case a: Animatable => a.start()
      case _ => // not animatable
    }
  }
}