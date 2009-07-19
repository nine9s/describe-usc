package com.android.deSCribe;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.RadioGroup;




public class Radio extends Activity implements RadioGroup.OnCheckedChangeListener,
        View.OnClickListener {

    private TextView mChoice;
    private RadioGroup mRadioGroup;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.radio);
        mRadioGroup = (RadioGroup) findViewById(R.id.menu);
        
        // test listening to checked change events        
        mChoice = (TextView) findViewById(R.id.choice);
        mChoice.setText("Your current selection is: " + deSCribe.user_pref);
        mRadioGroup.setOnCheckedChangeListener(this);
    }

    public void onCheckedChanged(RadioGroup group, int checkedId) {
        String selection = getString(R.string.radio_selection);        
        if(checkedId == R.id.Coarse)
        	deSCribe.user_pref = "Coarse";
        else
        	deSCribe.user_pref = "Fine";
        
        mChoice.setText(selection + deSCribe.user_pref);                      
        Intent myIntent = new Intent();
        myIntent.setClass(Radio.this, Tabs.class);
		startActivity(myIntent);
		finish();
        
    }

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}
    
}


