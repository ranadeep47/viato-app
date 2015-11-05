package in.viato.app.http.models.response;

import android.text.TextUtils;

import java.util.List;

/**
 * Created by saiteja on 04/11/15.
 */
public class Serviceability {
    private Boolean is_supported;
    private List<String> supported_localities;

    public Serviceability(Boolean is_supported, List<String> supported_localities) {
        this.is_supported = is_supported;
        this.supported_localities = supported_localities;
    }

    public Boolean getIs_supported() {
        return is_supported;
    }

    public void setIs_supported(Boolean is_supported) {
        this.is_supported = is_supported;
    }

    public String getSupported_localities() {
        if (supported_localities != null) {
            return TextUtils.join(", ", supported_localities);
        }
        return "";
    }

    public void setSupported_localities(List<String> supported_localities) {
        this.supported_localities = supported_localities;
    }
}
