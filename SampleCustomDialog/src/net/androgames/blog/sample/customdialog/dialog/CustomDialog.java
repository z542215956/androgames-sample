package net.androgames.blog.sample.customdialog.dialog;

import android.R;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

public class CustomDialog extends Dialog {

	public CustomDialog(Context context, int style) {
		super(context, style);
	}

	public static class Builder {

		private Context context;
		private String title;
		private String message;
		private String positiveButtonText;
		private String negativeButtonText;
		private View.OnClickListener positiveButtonClickListener;
		private View.OnClickListener negativeButtonClickListener;

		public Builder(Context context) {
			this.context = context;
		}

		public Builder setMessage(String message) {
			this.message = message;
			return this;
		}

		public Builder setTitle(int title) {
			this.title = (String) context.getText(title);
			return this;
		}

		public Builder setTitle(String title) {
			this.title = title;
			return this;
		}

		public Builder setPositiveButton(int positiveButtonText,
				View.OnClickListener positiveButtonClickListener) {
			this.positiveButtonText = (String) context
					.getText(positiveButtonText);
			this.positiveButtonClickListener = positiveButtonClickListener;
			return this;
		}

		public Builder setPositiveButton(String positiveButtonText,
				View.OnClickListener positiveButtonClickListener) {
			this.positiveButtonText = positiveButtonText;
			this.positiveButtonClickListener = positiveButtonClickListener;
			return this;
		}

		public Builder setNegativeButton(int negativeButtonText,
				View.OnClickListener negativeButtonClickListener) {
			this.negativeButtonText = (String) context
					.getText(negativeButtonText);
			this.negativeButtonClickListener = negativeButtonClickListener;
			return this;
		}

		public Builder setNegativeButton(String negativeButtonText,
				View.OnClickListener negativeButtonClickListener) {
			this.negativeButtonText = negativeButtonText;
			this.negativeButtonClickListener = negativeButtonClickListener;
			return this;
		}

		public CustomDialog create() {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			CustomDialog dialog = new CustomDialog(context,
					R.style.CustomDialog);
			View layout = inflater.inflate(R.layout.custom_dialog, null);
			dialog.addContentView(layout, new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			// title
			((TextView) layout.findViewById(R.id.title)).setText(title);
			// confirm
			if (positiveButtonText != null) {
				((Button) layout.findViewById(R.id.positiveButton))
						.setText(positiveButtonText);
				((Button) layout.findViewById(R.id.positiveButton))
						.setOnClickListener(positiveButtonClickListener);
			} else {
				layout.findViewById(R.id.positiveButton).setVisibility(
						View.GONE);
			}
			// cancel
			if (negativeButtonText != null) {
				((Button) layout.findViewById(R.id.negativeButton))
						.setText(negativeButtonText);
				((Button) layout.findViewById(R.id.negativeButton))
						.setOnClickListener(negativeButtonClickListener);
			} else {
				layout.findViewById(R.id.negativeButton).setVisibility(
						View.GONE);
			}
			// message
			((TextView) layout.findViewById(R.id.message)).setText(message);

			dialog.setContentView(layout);
			return dialog;
		}

	}

}