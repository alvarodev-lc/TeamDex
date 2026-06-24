package es.upm.mssde.pokedex.models;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.math.RoundingMode;

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
        float perc = (captureRate * 100) / 255;
        return BigDecimal.valueOf(perc).setScale(2, RoundingMode.HALF_DOWN).floatValue();
    }

    public void setCaptureRate(Float captureRate) {
        this.captureRate = captureRate;
    }

}