package cu.arr.etecsa.api.etecsa.promo.callback;

import cu.arr.etecsa.api.etecsa.promo.model.Promotion;
import java.util.List;

public interface PromotionCallback {

    void onResult(List<Promotion> promotions);
}
