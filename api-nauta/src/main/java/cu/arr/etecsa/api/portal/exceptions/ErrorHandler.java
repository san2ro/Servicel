package cu.arr.etecsa.api.portal.exceptions;

import androidx.annotation.Keep;

@Keep
public interface ErrorHandler {

    String handlerException(Throwable throwable);
}
