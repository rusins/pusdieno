import auth.CookieEnv
import com.google.inject.name.Named
import com.google.inject.{AbstractModule, Provides}
import com.mohiva.play.silhouette.api.crypto.{CookieSigner, Crypter, CrypterAuthenticatorEncoder}
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AuthenticatorService
import com.mohiva.play.silhouette.api.util._
import com.mohiva.play.silhouette.api.{Environment, EventBus, Silhouette, SilhouetteProvider}
import com.mohiva.play.silhouette.crypto.{JcaCookieSigner, JcaCookieSignerSettings, JcaCrypter, JcaCrypterSettings}
import com.mohiva.play.silhouette.impl.authenticators.{CookieAuthenticator, CookieAuthenticatorService, CookieAuthenticatorSettings}
import com.mohiva.play.silhouette.impl.providers.oauth2.state.{CookieStateProvider, CookieStateSettings}
import com.mohiva.play.silhouette.impl.providers.oauth2.{FacebookProvider, GoogleProvider}
import com.mohiva.play.silhouette.impl.providers.{OAuth2Info, OAuth2Settings, OAuth2StateProvider, SocialProviderRegistry}
import com.mohiva.play.silhouette.impl.util.{DefaultFingerprintGenerator, SecureRandomIDGenerator}
import com.mohiva.play.silhouette.persistence.daos.{DelegableAuthInfoDAO, InMemoryAuthInfoDAO}
import com.mohiva.play.silhouette.persistence.repositories.DelegableAuthInfoRepository
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._
import net.codingwell.scalaguice.ScalaModule
import play.api.Configuration
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.ws.WSClient
import services._
import services.daos.Users

/**
  * This class is a Guice module that tells Guice how to bind several
  * different types. This Guice module is created when the Play
  * application starts.
  *
  * Play will automatically use any class called `Module` that is in
  * the root package. You can create modules in other locations by
  * adding `play.modules.enabled` settings to the `application.conf`
  * configuration file.
  */
class Module extends AbstractModule with ScalaModule {

  override def configure() = {
    bind[java.time.Clock].toInstance(java.time.Clock.systemDefaultZone())
    // Ask Guice to create an instance of ApplicationTimer when the
    // application starts.
    bind(classOf[ApplicationTimer]).asEagerSingleton()

    bind(classOf[DatabasePopulator]).asEagerSingleton()

    // Silhouette
    bind[Silhouette[CookieEnv]].to[SilhouetteProvider[CookieEnv]]

    bind[Clock].toInstance(Clock())
    bind[FingerprintGenerator].toInstance(new DefaultFingerprintGenerator(false))
    bind[IDGenerator].toInstance(new SecureRandomIDGenerator())
    bind[DelegableAuthInfoDAO[OAuth2Info]].toInstance(new InMemoryAuthInfoDAO[OAuth2Info])
  }

  @Provides
  def provideEnvironment(userService: Users,
                         authenticatorService: AuthenticatorService[CookieAuthenticator],
                         eventBus: EventBus): Environment[CookieEnv] = {

    Environment[CookieEnv](
      userService,
      authenticatorService,
      Seq(),
      eventBus
    )
  }

  @Provides @Named("authenticator-cookie-signer")
  def provideAuthenticatorCookieSigner(configuration: Configuration): CookieSigner = {
    val config = configuration.underlying.as[JcaCookieSignerSettings]("silhouette.authenticator.cookie.signer")

    new JcaCookieSigner(config)
  }

  @Provides @Named("oauth2-state-cookie-signer")
  def provideOAuth2StageCookieSigner(configuration: Configuration): CookieSigner = {
    val config = configuration.underlying.as[JcaCookieSignerSettings]("silhouette.oauth2StateProvider.cookie.signer")

    new JcaCookieSigner(config)
  }

  @Provides @Named("authenticator-crypter")
  def provideAuthenticatorCrypter(configuration: Configuration): Crypter = {
    val config = configuration.underlying.as[JcaCrypterSettings]("silhouette.authenticator.crypter")

    new JcaCrypter(config)
  }

  @Provides
  def provideAuthenticatorService(
                                   @Named("authenticator-cookie-signer") cookieSigner: CookieSigner,
                                   @Named("authenticator-crypter") crypter: Crypter,
                                   fingerprintGenerator: FingerprintGenerator,
                                   idGenerator: IDGenerator,
                                   configuration: Configuration,
                                   clock: Clock): AuthenticatorService[CookieAuthenticator] = {

    val config = configuration.underlying.as[CookieAuthenticatorSettings]("silhouette.authenticator")
    val encoder = new CrypterAuthenticatorEncoder(crypter)

    new CookieAuthenticatorService(config, None, cookieSigner, encoder, fingerprintGenerator, idGenerator, clock)
  }

  @Provides
  def provideSocialProviderRegistry(g: GoogleProvider, f: FacebookProvider) = SocialProviderRegistry(Seq(g, f))

  @Provides
  def provideOAuth2StateProvider(
                                  idGenerator: IDGenerator,
                                  @Named("oauth2-state-cookie-signer") cookieSigner: CookieSigner,
                                  configuration: Configuration, clock: Clock): OAuth2StateProvider = {

    val settings = configuration.underlying.as[CookieStateSettings]("silhouette.oauth2StateProvider")
    new CookieStateProvider(settings, idGenerator, cookieSigner, clock)
  }

  @Provides
  def provideHTTPLayer(client: WSClient): HTTPLayer = new PlayHTTPLayer(client)

  @Provides
  def provideAuthInfoRepository(oauth2InfoDAO: DelegableAuthInfoDAO[OAuth2Info]): AuthInfoRepository = {

    new DelegableAuthInfoRepository(oauth2InfoDAO)
  }

  @Provides
  def provideGoogleProvider(
                             httpLayer: HTTPLayer,
                             stateProvider: OAuth2StateProvider,
                             configuration: Configuration): GoogleProvider = {

    new GoogleProvider(httpLayer, stateProvider, configuration.underlying.as[OAuth2Settings]("silhouette.google"))
  }

  @Provides
  def provideFacebookProvider(
                               httpLayer: HTTPLayer,
                               stateProvider: OAuth2StateProvider,
                               configuration: Configuration): FacebookProvider = {

    new FacebookProvider(httpLayer, stateProvider, configuration.underlying.as[OAuth2Settings]("silhouette.facebook"))
  }

}
