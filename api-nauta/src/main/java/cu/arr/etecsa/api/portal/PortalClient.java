package cu.arr.etecsa.api.portal;

import android.os.Build;
import androidx.annotation.Keep;
import cu.arr.etecsa.api.portal.models.CheckPayResponse;
import cu.arr.etecsa.api.portal.models.OkResponse;
import cu.arr.etecsa.api.portal.models.PaymentResponse;
import cu.arr.etecsa.api.portal.models.accounts.ProcessResponse;
import cu.arr.etecsa.api.portal.models.accounts.SendMoneyResponse;
import cu.arr.etecsa.api.portal.models.captcha.CaptchaResponse;
import cu.arr.etecsa.api.portal.models.login.LoginResponse;
import cu.arr.etecsa.api.portal.models.operations.OperationsResponse;
import cu.arr.etecsa.api.portal.models.profile.UpdateProfileResult;
import cu.arr.etecsa.api.portal.models.telephony.TelephonyResponse;
import cu.arr.etecsa.api.portal.models.users.UsersResponse;
import cu.arr.etecsa.api.portal.request.CheckPayRequest;
import cu.arr.etecsa.api.portal.request.account.SendMoneyRequest;
import cu.arr.etecsa.api.portal.request.account.StatusOpRequest;
import cu.arr.etecsa.api.portal.request.correo.ChangeMailPassword;
import cu.arr.etecsa.api.portal.request.login.LoginRequest;
import cu.arr.etecsa.api.portal.request.logout.LogoutRequest;
import cu.arr.etecsa.api.portal.request.nautahogar.PayNHRequest;
import cu.arr.etecsa.api.portal.request.operations.navigation.accounts.newaccount.OpCreateAccount;
import cu.arr.etecsa.api.portal.request.operations.navigation.accounts.password.OpResetPassword;
import cu.arr.etecsa.api.portal.request.password.ResetPassRequest;
import cu.arr.etecsa.api.portal.request.password.ValidateCodeRequest;
import cu.arr.etecsa.api.portal.request.password.ValidateUserRequest;
import cu.arr.etecsa.api.portal.request.profile.UpdateProfileRequest;
import cu.arr.etecsa.api.portal.request.register.CodeRequest;
import cu.arr.etecsa.api.portal.request.register.CreateAccountRequest;
import cu.arr.etecsa.api.portal.request.register.RegisterRequest;
import cu.arr.etecsa.api.portal.request.telephony.TelephonyRequest;
import cu.arr.etecsa.api.portal.request.topup.TopupCuponRequest;
import cu.arr.etecsa.api.portal.request.topup.TopupOnlineRequest;
import cu.arr.etecsa.api.portal.request.users.UsersRequest;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

@Keep
public interface PortalClient {

    /**
     * Obtiene el código CAPTCHA y el ID de la solicitud. Este código se utilizará para validar la
     * interacción del usuario en procesos como el registro o restablecimiento de contraseñas.
     *
     * @return Un objeto Single que contiene la respuesta del CAPTCHA (CaptchaResponse).
     */
    @Headers({"Content-Type: application/json"})
    @GET("/captcha/captcha")
    Single<CaptchaResponse> getCaptcha();

    /**
     * Realiza una solicitud @POST para iniciar sesión y retorna una respuesta con los datos del
     * usuario recibidos en formato Json.
     *
     * @return Un objeto Single que contiene la respuesta del servidor (LoginResponse).
     */
    @Headers({"usernameApp: portal"})
    @POST("/login")
    Single<LoginResponse> login(@Body LoginRequest body, @Header("passwordApp") String passwordApp);

    /**
     * Realiza una solicitud @POST para obtener los servicios móviles y retorna una respuesta con
     * los datos del usuario recibidos en formato Json.
     *
     * @return Un objeto Single que contiene la respuesta del servidor (UsersResponse).
     */
    @Headers({"usernameApp: portal"})
    @POST("/users")
    Single<UsersResponse> authUser(
            @Body UsersRequest body,
            @Header("passwordApp") String passwordApp,
            @Header("Authorization") String authorization);

    /**
     * Realiza una solicitud @POST para cerrar sesión en el portal nauta y retorna una respuesta
     * json con la confirmación de que se ha cerrado con éxito.
     *
     * @return un objeto Single que contiene la respuesta del servidor.
     */
    @Headers({"usernameApp: portal"})
    @POST("/users/logout")
    Single<OkResponse> logOut(
            @Body LogoutRequest body,
            @Header("passwordApp") String passwordApp,
            @Header("Authorization") String authorization);

    /**
     * Realiza una solicitud @POST para actualizar datos del perfil en el portal nauta y retorna una
     * respuesta json con la confirmación de que se ha cerrado con éxito.
     *
     * @return un objeto Single que contiene la respuesta del servidor.
     */
    @Headers({"usernameApp: portal"})
    @POST("/users/updateprofile")
    Single<UpdateProfileResult> updateProfile(
            @Body UpdateProfileRequest body,
            @Header("passwordApp") String passwordApp,
            @Header("Authorization") String authorization);

    /**
     * Realiza una solicitud @POST para recargar cualquier cuenta en línea, retorna una respuesta
     * json con la información del pago, cantidad, descuento, total, link de Transfermóvil.
     *
     * @return un objeto Single que contiene la respuesta del servidor.
     */
    @Headers({"usernameApp: portal"})
    @POST("/payment/bz/payorder")
    Single<PaymentResponse> topUpOnlineAccount(
            @Body TopupOnlineRequest body,
            @Header("passwordApp") String passwordApp,
            @Header("Authorization") String authorization);

    /**
     * Realiza una solicitud @POST para pagar una cuenta nauta hogar, retorna una respuesta json con
     * la información del pago, cantidad, descuento, total, link de Transfermóvil.
     *
     * @return un objeto Single que contiene la respuesta del servidor.
     */
    @Headers({"usernameApp: portal"})
    @POST("/payment/bz/payorder")
    Single<PaymentResponse> payNautaHogar(
            @Body PayNHRequest body,
            @Header("passwordApp") String passwordApp,
            @Header("Authorization") String authorization);

    /**
     * Realiza una solicitud @POST para comprobar el estado de pago en linea, retorna una respuesta
     * json con la información del estado.
     *
     * @return un objeto Single que contiene la respuesta del servidor.
     */
    @Headers({"usernameApp: portal"})
    @POST("/payment/bz/checkPayment")
    Single<CheckPayResponse> checkPayment(
            @Body CheckPayRequest body,
            @Header("passwordApp") String passwordApp,
            @Header("Authorization") String authorization);

    /**
     * Realiza una solicitud @POST para transferir saldo desde cuentas nauta, retorna una respuesta
     * json con la información del estado de transferencia.
     *
     * @return un objeto Single que contiene la respuesta del servidor.
     */
    @Headers({"usernameApp: portal"})
    @POST("/operations")
    Single<SendMoneyResponse> sendCurrency(
            @Body SendMoneyRequest body,
            @Header("passwordApp") String passwordApp,
            @Header("Authorization") String authorization);

    /**
     * Realiza una solicitud @POST para comprobar el estado de la transferencia, retorna una
     * respuesta json con la información del estado de transferencia.
     *
     * @return un objeto Single que contiene la respuesta del servidor.
     */
    @Headers({"usernameApp: portal"})
    @POST("/consults/process_response")
    Single<ProcessResponse> statusOp(
            @Body StatusOpRequest body,
            @Header("passwordApp") String passwordApp,
            @Header("Authorization") String authorization);

    /**
     * Realiza una solicitud @POST para cambiar la contraseña de la cuentas nauta, retorna una
     * respuesta json con la información del estado.
     *
     * @return un objeto Single que contiene la respuesta del servidor.
     */
    @Headers({"usernameApp: portal"})
    @POST("/operations")
    Single<OperationsResponse> opChangePassword(
            @Body OpResetPassword body,
            @Header("passwordApp") String passwordApp,
            @Header("Authorization") String authorization);

    /**
     * Realiza una solicitud @POST para crear una cuenta de navegación, retorna una respuesta de si
     * el registro fue exitoso o no.
     *
     * @return un objeto Single que contiene la respuesta del servidor.
     */
    @Headers({"usernameApp: portal"})
    @POST("/operations")
    Single<OperationsResponse> opCreateAccount(
            @Body OpCreateAccount body,
            @Header("passwordApp") String passwordApp,
            @Header("Authorization") String authorization);

    /**
     * Realiza una solicitud @POST para registrarse en el portal nauta, retorna una respuesta de si
     * el registro fue exitoso o no.
     *
     * @return un objeto Single que contiene la respuesta del servidor.
     */
    @POST("/reset/registeruser")
    Single<OkResponse> registerUser(@Body RegisterRequest body);

    // validar codigo de registro
    @POST("/reset/validateCodeIdentidad")
    Single<OkResponse> validateCode(@Body CodeRequest body);

    // crear contraseña
    @POST("/reset/createuser")
    Single<OkResponse> createUser(@Body CreateAccountRequest body);

    /**
     * Realiza una solicitud @POST para validar el usuario en el portal nauta, retorna una respuesta
     * si la validación fue exitosa o no.
     *
     * @return un objeto Single que contiene la respuesta del servidor.
     */
    @POST("/reset/validateUser")
    Single<OkResponse> validateUser(@Body ValidateUserRequest body);

    // validar codigo
    @POST("/reset/validateCodeUsuario")
    Single<OkResponse> validateCodePass(@Body ValidateCodeRequest body);

    // cambiar contraseña
    @POST("/reset/resetPass")
    Single<OkResponse> resetPassword(@Body ResetPassRequest body);

    // recargar cuenta nauta con cupon
    @Headers({"usernameApp: portal"})
    @POST("/operations")
    Single<OkResponse> topupCupon(
            @Body TopupCuponRequest body,
            @Header("passwordApp") String passwordApp,
            @Header("Authorization") String authorization);

    // consultar factura telefónica
    @Headers({"usernameApp: portal"})
    @POST("/consults")
    Single<TelephonyResponse> fixedFactura(
            @Body TelephonyRequest body,
            @Header("passwordApp") String passwordApp,
            @Header("Authorization") String authorization);

    // restablecer contraseña de correo nauta
    @Headers({"usernameApp: portal"})
    @POST("/operations")
    Single<OperationsResponse> changeMailPassword(
            @Body ChangeMailPassword body,
            @Header("passwordApp") String passwordApp,
            @Header("Authorization") String authorization);
}
