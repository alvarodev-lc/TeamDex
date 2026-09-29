package es.upm.mssde.pokedex;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;

import com.google.android.material.chip.Chip;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import es.upm.mssde.pokedex.models.NamedApiResource;

public final class ViewUtils {

    private ViewUtils() {
    }

    public static int dp(Context context, float value) {
        return Math.round(value * context.getResources().getDisplayMetrics().density);
    }

    /** White or dark text, whichever contrasts more with the given opaque background. */
    public static int readableTextOn(Context context, int background) {
        int darkText = ContextCompat.getColor(context, R.color.textPrimary);
        return ColorUtils.calculateContrast(Color.WHITE, background)
                >= ColorUtils.calculateContrast(darkText, background) ? Color.WHITE : darkText;
    }

    public static Chip createChip(Context context, String text, int background, int textColor) {
        Chip chip = new Chip(context);
        chip.setText(text);
        chip.setChipBackgroundColor(ColorStateList.valueOf(background));
        chip.setTextColor(textColor);
        chip.setTypeface(Typeface.DEFAULT_BOLD);
        chip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        chip.setChipStrokeWidth(0f);
        chip.setChipMinHeight(dp(context, 30));
        chip.setEnsureMinTouchTargetSize(false);
        chip.setClickable(false);
        chip.setFocusable(false);
        return chip;
    }

    public static Chip createTypeChip(Context context, String type, String suffix) {
        int background = PokeFormat.typeColor(type);
        String text = NamedApiResource.prettify(type) + (suffix != null ? "  " + suffix : "");
        return createChip(context, text, background, readableTextOn(context, background));
    }

    /** Compact type label for tight spaces such as team slots. */
    public static TextView createTypePill(Context context, String type) {
        int background = PokeFormat.typeColor(type);
        TextView pill = new TextView(context);
        pill.setText(NamedApiResource.prettify(type));
        pill.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        pill.setTypeface(Typeface.DEFAULT_BOLD);
        pill.setTextColor(readableTextOn(context, background));
        pill.setPadding(dp(context, 8), dp(context, 2), dp(context, 8), dp(context, 2));
        GradientDrawable shape = new GradientDrawable();
        shape.setColor(background);
        shape.setCornerRadius(dp(context, 12));
        pill.setBackground(shape);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(dp(context, 2), 0, dp(context, 2), 0);
        pill.setLayoutParams(params);
        return pill;
    }

    public static View createStatRow(LayoutInflater inflater, ViewGroup parent, String label, int value) {
        View row = inflater.inflate(R.layout.item_stat_row, parent, false);
        ((TextView) row.findViewById(R.id.stat_label)).setText(label);
        ((TextView) row.findViewById(R.id.stat_value)).setText(String.valueOf(value));
        LinearProgressIndicator bar = row.findViewById(R.id.stat_bar);
        bar.setIndicatorColor(PokeFormat.statColor(value));
        bar.setProgressCompat(value, true);
        return row;
    }

    /** Loads the official artwork, falling back to the in-game sprite when a form has no artwork. */
    public static void loadArtwork(ImageView target, String artworkUrl, String fallbackUrl) {
        Picasso.get().load(artworkUrl).into(target, new Callback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onError(Exception e) {
                Picasso.get().load(fallbackUrl).into(target);
            }
        });
    }
}
