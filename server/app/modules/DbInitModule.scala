package modules

import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule
import services.DatabasePopulator

class DbInitModule extends AbstractModule with ScalaModule {

  override def configure(): Unit = {
    bind(classOf[DatabasePopulator]).asEagerSingleton()
  }

}
