package com.jobsearchtry;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import java.util.Calendar;

public class DatePicker extends RelativeLayout {
	private final int startYear = 1600;
	private final int endYear = 2400;
	private View myPickerView;
	private EditText month_display;
	private EditText date_display;
	private EditText year_display;
	static String dayName;
	private int Weekdays_int;
	private Calendar cal;

	public DatePicker(Context context) {
		this(context, null);
		init(context);
	}

	public DatePicker(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DatePicker(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		myPickerView = View.inflate(context,R.layout.datepicker, null);
		this.addView(myPickerView);
		initializeReference();
	}

	private void init(Context mContext) {
		myPickerView = View.inflate(mContext,R.layout.datepicker, null);
		this.addView(myPickerView);
		initializeReference();
	}

	private void initializeReference() {
		Button month_plus = (Button) myPickerView.findViewById(R.id.am_month_plus);
		month_plus.setOnClickListener(month_plus_listener);
		month_display = (EditText) myPickerView
				.findViewById(R.id.am_month_display);
		Button month_minus = (Button) myPickerView.findViewById(R.id.am_month_minus);
		month_minus.setOnClickListener(month_minus_listener);
		Button date_plus = (Button) myPickerView.findViewById(R.id.am_date_plus);
		date_plus.setOnClickListener(date_plus_listener);
		date_display = (EditText) myPickerView
				.findViewById(R.id.am_date_display);
		date_display.addTextChangedListener(date_watcher);
		Button date_minus = (Button) myPickerView.findViewById(R.id.am_date_minus);
		date_minus.setOnClickListener(date_minus_listener);
		Button year_plus = (Button) myPickerView.findViewById(R.id.am_year_plus);
		year_plus.setOnClickListener(year_plus_listener);
		year_display = (EditText) myPickerView
				.findViewById(R.id.am_year_display);
		year_display.setOnFocusChangeListener(mLostFocusYear);
		year_display.addTextChangedListener(year_watcher);
		Button year_minus = (Button) myPickerView.findViewById(R.id.am_year_minus);
		year_minus.setOnClickListener(year_minus_listener);
		cal = Calendar.getInstance();
		//initData(getDob);
		initFilterNumericDigit();
	}

	 void initData(String getDob) {
		String[] out = getDob.split("-");
		int getmonth = Integer.parseInt(out[1]) - 1;
		month_display.setText(months[getmonth]);
		date_display.setText(String.valueOf(Integer.parseInt(out[0])));
		year_display.setText(String.valueOf(Integer.parseInt(out[2])));
		//Weekdays_int = cal.get(Calendar.DAY_OF_WEEK);
		//WeekDays();
	}

	private void initFilterNumericDigit() {
		try {
			date_display.setFilters(new InputFilter[]{new InputFilterMinMax(
					1, cal.getActualMaximum(Calendar.DAY_OF_MONTH))});
			InputFilter[] filterArray_year = new InputFilter[1];
			filterArray_year[0] = new InputFilter.LengthFilter(4);
			year_display.setFilters(filterArray_year);
		} catch (Exception ignored) {
		}
	}

	private void changeFilter() {
		try {
			date_display.setFilters(new InputFilter[]{new InputFilterMinMax(
					1, cal.getActualMaximum(Calendar.DAY_OF_MONTH))});
		} catch (Exception e) {
			date_display.setText("" + cal.get(Calendar.DAY_OF_MONTH));
		}
	}

	private static final String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul",
			"Aug", "Sep", "Oct", "Nov", "Dec"};
	private final OnClickListener month_plus_listener = new OnClickListener() {
		public void onClick(View v) {
			try {
				cal.add(Calendar.MONTH, 1);
				month_display.setText(months[cal.get(Calendar.MONTH)]);
				year_display.setText(String.valueOf(cal.get(Calendar.YEAR)));
				date_display.setText(String.valueOf(cal
						.get(Calendar.DAY_OF_MONTH)));
				Weekdays_int = cal.get(Calendar.DAY_OF_WEEK);
				WeekDays();
				changeFilter();
				sendToListener();
			} catch (Exception ignored) {
			}
		}
	};
	private final OnClickListener month_minus_listener = new OnClickListener() {
		public void onClick(View v) {
			try {
				cal.add(Calendar.MONTH, -1);
				month_display.setText(months[cal.get(Calendar.MONTH)]);
				year_display.setText(String.valueOf(cal.get(Calendar.YEAR)));
				date_display.setText(String.valueOf(cal
						.get(Calendar.DAY_OF_MONTH)));
				Weekdays_int = cal.get(Calendar.DAY_OF_WEEK);
				WeekDays();
				changeFilter();
				sendToListener();
			} catch (Exception ignored) {
			}
		}
	};
	private final OnClickListener date_plus_listener = new OnClickListener() {
		public void onClick(View v) {
			try {
				date_display.requestFocus();
				cal.add(Calendar.DAY_OF_MONTH, 1);
				month_display.setText(months[cal.get(Calendar.MONTH)]);
				year_display.setText(String.valueOf(cal.get(Calendar.YEAR)));
				date_display.setText(String.valueOf(cal
						.get(Calendar.DAY_OF_MONTH)));
				Weekdays_int = cal.get(Calendar.DAY_OF_WEEK);
				WeekDays();
				sendToListener();
			} catch (Exception ignored) {
			}
		}
	};
	private final OnClickListener date_minus_listener = new OnClickListener() {
		public void onClick(View v) {
			try {
				date_display.requestFocus();
				cal.add(Calendar.DAY_OF_MONTH, -1);
				month_display.setText(months[cal.get(Calendar.MONTH)]);
				year_display.setText(String.valueOf(cal.get(Calendar.YEAR)));
				date_display.setText(String.valueOf(cal
						.get(Calendar.DAY_OF_MONTH)));
				Weekdays_int = cal.get(Calendar.DAY_OF_WEEK);
				WeekDays();
				sendToListener();
			} catch (Exception ignored) {
			}
		}
	};
	private final OnClickListener year_plus_listener = new OnClickListener() {
		public void onClick(View v) {
			try {
				year_display.requestFocus();
				if (cal.get(Calendar.YEAR) >= endYear) {
					cal.set(Calendar.YEAR, startYear);
				} else {
					cal.add(Calendar.YEAR, +1);
				}
				month_display.setText(months[cal.get(Calendar.MONTH)]);
				year_display.setText(String.valueOf(cal.get(Calendar.YEAR)));
				date_display.setText(String.valueOf(cal
						.get(Calendar.DAY_OF_MONTH)));
				Weekdays_int = cal.get(Calendar.DAY_OF_WEEK);
				WeekDays();
				changeFilter();
				sendToListener();
			} catch (Exception ignored) {
			}
		}
	};
	private final OnClickListener year_minus_listener = new OnClickListener() {
		public void onClick(View v) {
			try {
				year_display.requestFocus();
				if (cal.get(Calendar.YEAR) <= startYear) {
					cal.set(Calendar.YEAR, endYear);
				} else {
					cal.add(Calendar.YEAR, -1);
				}
				month_display.setText(months[cal.get(Calendar.MONTH)]);
				year_display.setText(String.valueOf(cal.get(Calendar.YEAR)));
				date_display.setText(String.valueOf(cal
						.get(Calendar.DAY_OF_MONTH)));
				Weekdays_int = cal.get(Calendar.DAY_OF_WEEK);
				WeekDays();
				changeFilter();
				sendToListener();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	private class InputFilterMinMax implements InputFilter {
		private final int min;
		private final int max;

		 InputFilterMinMax(int min, int max) {
			this.min = min;
			this.max = max;
		}

		public CharSequence filter(CharSequence source, int start, int end,
								   Spanned dest, int dstart, int dend) {
			try {
				int input = Integer.parseInt(dest.toString()
						+ source.toString());
				if (isInRange(min, max, input)) {
					return null;
				}
			} catch (NumberFormatException ignored) {
			}
			return "";
		}

		private boolean isInRange(int a, int b, int c) {
			return b > a ? c >= a && c <= b : c >= b && c <= a;
		}
	}

	public void reset() {
		cal = Calendar.getInstance();
		initFilterNumericDigit();
		//initData(getDob);
	}

	synchronized private void sendToListener() {
		if (mDateWatcher != null) {
			mDateWatcher.onDateChanged(cal);
		}
	}
	 void setDateChangedListener(DateWatcher listener) {
		this.mDateWatcher = listener;
	}
	private final OnFocusChangeListener mLostFocusYear = new OnFocusChangeListener() {
		public void onFocusChange(View v, boolean hasFocus) {
			if (!hasFocus) {
				year_display.setText(String.valueOf(cal.get(Calendar.YEAR)));
			}
		}
	};
	private final TextWatcher date_watcher = new TextWatcher() {
		public void onTextChanged(CharSequence s, int start, int before,
								  int count) {
		}

		public void beforeTextChanged(CharSequence s, int start, int count,
									  int after) {
		}

		public void afterTextChanged(Editable s) {
			try {
				if (s.toString().length() > 0) {
					cal.set(Calendar.DAY_OF_MONTH,
							Integer.parseInt(s.toString()));
					month_display.setText(months[cal.get(Calendar.MONTH)]);
					sendToListener();
				}
			} catch (NumberFormatException ignored) {
			}
		}
	};
	private final TextWatcher year_watcher = new TextWatcher() {
		public void onTextChanged(CharSequence s, int start, int before,
								  int count) {
		}

		public void beforeTextChanged(CharSequence s, int start, int count,
									  int after) {
		}

		public void afterTextChanged(Editable s) {
			try {
				if (s.toString().length() == 4) {
					int year = Integer.parseInt(s.toString());
					if (year > endYear) {
						cal.set(Calendar.YEAR, endYear);
					} else if (year < startYear) {
						cal.set(Calendar.YEAR, startYear);
					} else {
						cal.set(Calendar.YEAR, year);
					}
				}
				sendToListener();
			} catch (Exception ignored) {
			}
		}
	};
	private DateWatcher mDateWatcher = null;

	interface DateWatcher {
		void onDateChanged(Calendar c);
	}

	private void WeekDays() {
		if (Weekdays_int == 1) {
			dayName = "Sunday" + "," + " " + months[cal.get(Calendar.MONTH)]
					+ " " + String.valueOf(cal.get(Calendar.DAY_OF_MONTH))
					+ "," + " " + String.valueOf(cal.get(Calendar.YEAR));
		} else if (Weekdays_int == 2) {
			dayName = "Monday" + "," + " " + months[cal.get(Calendar.MONTH)]
					+ " " + String.valueOf(cal.get(Calendar.DAY_OF_MONTH))
					+ "," + " " + String.valueOf(cal.get(Calendar.YEAR));
		} else if (Weekdays_int == 3) {
			dayName = "Tuesday" + "," + " " + months[cal.get(Calendar.MONTH)]
					+ " " + String.valueOf(cal.get(Calendar.DAY_OF_MONTH))
					+ "," + " " + String.valueOf(cal.get(Calendar.YEAR));
		} else if (Weekdays_int == 4) {
			dayName = "Wednesday" + "," + " " + months[cal.get(Calendar.MONTH)]
					+ " " + String.valueOf(cal.get(Calendar.DAY_OF_MONTH))
					+ "," + " " + String.valueOf(cal.get(Calendar.YEAR));
		} else if (Weekdays_int == 5) {
			dayName = "Thursday" + "," + " " + months[cal.get(Calendar.MONTH)]
					+ " " + String.valueOf(cal.get(Calendar.DAY_OF_MONTH))
					+ "," + " " + String.valueOf(cal.get(Calendar.YEAR));
		} else if (Weekdays_int == 6) {
			dayName = "Friday" + "," + " " + months[cal.get(Calendar.MONTH)]
					+ " " + String.valueOf(cal.get(Calendar.DAY_OF_MONTH))
					+ "," + " " + String.valueOf(cal.get(Calendar.YEAR));
		} else if (Weekdays_int == 7) {
			dayName = "Saturday" + "," + " " + months[cal.get(Calendar.MONTH)]
					+ " " + String.valueOf(cal.get(Calendar.DAY_OF_MONTH))
					+ "," + " " + String.valueOf(cal.get(Calendar.YEAR));
		}
	}
}
