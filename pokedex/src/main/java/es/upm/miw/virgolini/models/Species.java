package es.upm.miw.virgolini.models;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

@Generated("jsonschema2pojo")
public class Species {

    @SerializedName("base_happiness")
    @Expose
    private Integer baseHappiness;
    @SerializedName("capture_rate")
    @Expose
    private Float captureRate;

    public Integer getBaseHappiness() {
        return baseHappiness;
    }

    public void setBaseHappiness(Integer baseHappiness) {
        this.baseHappiness = baseHappiness;
    }

    public Float getCaptureRate() {
        // 255 is 100%, parse integer to percentage
        Float perc = (Float) (captureRate * 100) / 255;
        captureRate = BigDecimal.valueOf(
                perc).setScale(2, BigDecimal.ROUND_HALF_DOWN).floatValue();
        return captureRate;
    }

    public void setCaptureRate(Float captureRate) {
        this.captureRate = captureRate;
    }

}