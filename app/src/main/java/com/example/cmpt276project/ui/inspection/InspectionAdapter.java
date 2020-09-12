/*
    This is InspectionAdapter class implementation.
    Last Modified Date: 2020/07/08

    This class requires to input 6 parameters.
    1. context: Context
    2. allCritical: boolean[][]
    3. allNatures: int[][]
    4. allShortDescription String[]
    5. allLongDescription String[]
    6. images: int[]

    This class uses for to be a RecyclerView Adapter to display detail of each violation
    in the inspection.
 */

// Package
package com.example.cmpt276project.ui.inspection;

// Import
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cmpt276project.R;

// InspectionAdapter Class
public class InspectionAdapter extends RecyclerView.Adapter<InspectionAdapter.InspectionViewHolder> {

    private Context context;
    private boolean[] allCritical;
    private boolean[][] allNatures;
    private String[] allShortDescriptions;
    private String[] allLongDescriptions;
    private int[] images;

    // Constructor
    public InspectionAdapter(Context context, boolean[] allCritical, boolean[][] allNatures, String[] allShortDescriptions, String[] allLongDescriptions, int[] images) {
        this.context = context;
        this.allCritical = allCritical;
        this.allNatures = allNatures;
        this.allShortDescriptions = allShortDescriptions;
        this.allLongDescriptions = allLongDescriptions;
        this.images = images;
    }

    // Link to violation_row.xml document
    @NonNull
    @Override
    public InspectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.violation_row, parent, false);
        return new InspectionViewHolder(view);
    }

    // Set functions to each row
    @Override
    public void onBindViewHolder(@NonNull InspectionViewHolder holder, int position) {

        final int POSITION_INDEX = position;

        // Set the number of violations TextView
        holder.numViolations_textView.setText("" + (position + 1) + ":");

        // Set the critical TextView and ImageView
        if (allCritical[position] == true) {
            holder.critical_textView.setText(R.string.inspection_adapter_critical);
            holder.critical_textView.setTextColor(Color.parseColor("#FF0000"));
            holder.isCritical_imageView.setImageResource(images[5]);
        }
        else {
            holder.critical_textView.setText(R.string.inspection_adapter_not_critical);
            holder.isCritical_imageView.setImageResource(0);
        }

        // Set the Nature ImageView
        ImageView[] imageViews = {holder.equipment_imageView, holder.utensil_imageView,
                holder.food_imageView, holder.pest_imageView, holder.employee_imageView};

        for (int i = 0; i < 5; i++) {
            if (allNatures[position][i] == true) {
                imageViews[i].setImageResource(images[i]);
            }
            else {
                imageViews[i].setImageResource(0);
            }
        }

        // Set the Short Description TextView
        holder.problem_textView.setText(allShortDescriptions[position]);

        // Set each row of layout to make Toast
        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, getTranslatedString(allLongDescriptions[POSITION_INDEX]), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getTranslatedString(String longDescription) {
        switch (longDescription.charAt(0)) {
            case 'A':
                if (longDescription.equals("Adequate handwashing stations not available for employees [s. 21(4)]")) {
                    return context.getString(R.string.violation_message_a1);
                }
                break;
            case 'C':
                if (longDescription.equals("Chemicals, cleansers, & similar agents stored or labeled improperly [s. 27]")) {
                    return context.getString(R.string.violation_message_c1_1);
                }
                else if (longDescription.equals("Chemicals cleansers & similar agents stored or labeled improperly [s. 27]")) {
                    return context.getString(R.string.violation_message_c1_2);
                }
                else if (longDescription.equals("Cold potentially hazardous food stored/displayed above 4 ?C. [s. 14(2)]")) {
                    return context.getString(R.string.violation_message_c2);
                }
                else if (longDescription.equals("Cold potentially hazardous food stored/displayed above 4 °C. [s. 14(2)]")) {
                    return context.getString(R.string.violation_message_c2);
                }
                else if (longDescription.equals("Conditions observed that may allow entrance/harbouring/breeding of pests [s. 26(b),(c)]")) {
                    return context.getString(R.string.violation_message_c3_1);
                }
                else if (longDescription.equals("Conditions observed that may allow entrance/harbouring/breeding of pests [s. 26(b)(c)]")) {
                    return context.getString(R.string.violation_message_c3_2);
                }
                break;
            case 'E':
                if (longDescription.equals("Employee does not wash hands properly or at adequate frequency [s. 21(3)]")) {
                    return context.getString(R.string.violation_message_e1);
                }
                else if (longDescription.equals("Employee lacks good personal hygiene, clean clothing and hair control [s. 21(1)]")) {
                    return context.getString(R.string.violation_message_e2_1);
                }
                else if (longDescription.equals("Employee lacks good personal hygiene clean clothing and hair control [s. 21(1)]")) {
                    return context.getString(R.string.violation_message_e2_2);
                }
                else if (longDescription.equals("Employee smoking in food preparation/processing/storage areas [s. 21(2)]")) {
                    return context.getString(R.string.violation_message_e3);
                }
                else if (longDescription.equals("Equipment/facilities/hot & cold water for sanitary maintenance not adequate [s. 17(3); s. 4(1)(f)]")) {
                    return context.getString(R.string.violation_message_e4);
                }
                else if (longDescription.equals("Equipment/utensils/food contact surfaces are not in good working order [s. 16(b)]")) {
                    return context.getString(R.string.violation_message_e5);
                }
                else if (longDescription.equals("Equipment/utensils/food contact surfaces are not of suitable design/material [s. 16; s. 19]")) {
                    return context.getString(R.string.violation_message_e6);
                }
                else if (longDescription.equals("Equipment/utensils/food contact surfaces not maintained in sanitary condition [s. 17(1)]")) {
                    return context.getString(R.string.violation_message_e7);
                }
                else if (longDescription.equals("Equipment/utensils/food contact surfaces not properly washed and sanitized [s. 17(2)]")) {
                    return context.getString(R.string.violation_message_e8);
                }
                break;
            case 'F':
                if (longDescription.equals("Failure to hold a valid permit while operating a food service establishment [s. 8(1)]")) {
                    return context.getString(R.string.violation_message_f1);
                }
                else if (longDescription.equals("Food contaminated or unfit for human consumption [s. 13]")) {
                    return context.getString(R.string.violation_message_f2);
                }
                else if (longDescription.equals("Food not cooked or reheated in a manner that makes it safe to eat [s. 14(1)]")) {
                    return context.getString(R.string.violation_message_f3);
                }
                else if (longDescription.equals("Food not cooled in an acceptable manner [s. 12(a)]")) {
                    return context.getString(R.string.violation_message_f4);
                }
                else if (longDescription.equals("Food not processed in a manner that makes it safe to eat [s. 14(1)]")) {
                    return context.getString(R.string.violation_message_f5);
                }
                else if (longDescription.equals("Food not protected from contamination [s. 12(a)]")) {
                    return context.getString(R.string.violation_message_f6);
                }
                else if (longDescription.equals("Food not thawed in an acceptable manner [s. 14(2)]")) {
                    return context.getString(R.string.violation_message_f7);
                }
                else if (longDescription.equals("Food premises not maintained in a sanitary condition [s. 17(1)]")) {
                    return context.getString(R.string.violation_message_f8);
                }
                else if (longDescription.equals("Foods obtained from unapproved sources [s. 11]")) {
                    return context.getString(R.string.violation_message_f9);
                }
                else if (longDescription.equals("Frozen potentially hazardous food stored/displayed above -18 ?C. [s. 14(3)]")) {
                    return context.getString(R.string.violation_message_f10);
                }
                else if (longDescription.equals("Frozen potentially hazardous food stored/displayed above -18 °C. [s. 14(3)]")) {
                    return context.getString(R.string.violation_message_f10);
                }
                break;
            case 'H':
                if (longDescription.equals("Hot potentially hazardous food stored/displayed below 60 ?C. [s. 14(2)]")) {
                    return context.getString(R.string.violation_message_h1);
                }
                else if (longDescription.equals("Hot potentially hazardous food stored/displayed below 60 °C. [s. 14(2)]")) {
                    return context.getString(R.string.violation_message_h1);
                }
                break;
            case 'I':
                if (longDescription.equals("In operator?s absence, no staff on duty has FOODSAFE Level 1 or equivalent [s. 10(2)]")) {
                    return context.getString(R.string.violation_message_i1_1);
                }
                else if (longDescription.equals("In operator’s absence no staff on duty has FOODSAFE Level 1 or equivalent [s. 10(2)]")) {
                    return context.getString(R.string.violation_message_i1_2);
                }
                else if (longDescription.equals("Items not required for food premises operation being stored on the premises [s. 18]")) {
                    return context.getString(R.string.violation_message_i2);
                }
                break;
            case 'L':
                if (longDescription.equals("Live animal on the premises, excluding guide animal in approved areas [s. 25(1)]")) {
                    return context.getString(R.string.violation_message_l1);
                }
                break;
            case 'O':
                if (longDescription.equals("Operation of an unapproved food premises [s. 6(1)]")) {
                    return context.getString(R.string.violation_message_o1);
                }
                else if (longDescription.equals("Operator does not have FOODSAFE Level 1 or Equivalent [s. 10(1)]")) {
                    return context.getString(R.string.violation_message_o2);
                }
                else if (longDescription.equals("Operator has not provided acceptable written food handling procedures [s. 23]")) {
                    return context.getString(R.string.violation_message_o3);
                }
                else if (longDescription.equals("Operator has not provided acceptable written sanitation procedures [s. 24]")) {
                    return context.getString(R.string.violation_message_o4);
                }
                break;
            case 'P':
                if (longDescription.equals("Permit not posted in a conspicuous location [s. 8(7)]")) {
                    return context.getString(R.string.violation_message_p1);
                }
                else if (longDescription.equals("Plans/construction/alterations not in accordance with the Regulation [s. 3; s. 4]")) {
                    return context.getString(R.string.violation_message_p2);
                }
                else if (longDescription.equals("Premises not free of pests [s. 26(a)]")) {
                    return context.getString(R.string.violation_message_p3);
                }
                else if (longDescription.equals("Premises not maintained as per approved plans [s. 6(1)(b)]")) {
                    return context.getString(R.string.violation_message_p4);
                }
                break;
            case 'R':
                if (longDescription.equals("Refrigeration units and hot holding equipment lack accurate thermometers [s. 19(2)]")) {
                    return context.getString(R.string.violation_message_r1);
                }
                break;
            case 'S':
                if (longDescription.equals("Single use containers & utensils are used more than once [s. 20]")) {
                    return context.getString(R.string.violation_message_s1);
                }
                break;
            default:
                Log.e("InspectionAdapter", "Cannot find this description - " + longDescription);
                return longDescription;
        }
        return longDescription;
    }

    // Set the number of rows
    @Override
    public int getItemCount() {
        return allCritical.length;
    }

    // Set all the TextViews, ImageViews, and ConstrainLayout
    public class InspectionViewHolder extends RecyclerView.ViewHolder {

        TextView numViolations_textView, critical_textView, problem_textView;
        ImageView equipment_imageView, utensil_imageView, food_imageView, pest_imageView,
                employee_imageView, isCritical_imageView;
        ConstraintLayout constraintLayout;

        public InspectionViewHolder(@NonNull View itemView) {
            super(itemView);

            // TextView
            numViolations_textView = itemView.findViewById(R.id.numViolations_textView);
            critical_textView = itemView.findViewById(R.id.critical_textView);
            problem_textView = itemView.findViewById(R.id.problem_textView);

            // ImageView
            equipment_imageView = itemView.findViewById(R.id.equipment_imageView);
            utensil_imageView = itemView.findViewById(R.id.utensil_imageView);
            food_imageView = itemView.findViewById(R.id.food_imageView);
            pest_imageView = itemView.findViewById(R.id.pest_imageView);
            employee_imageView = itemView.findViewById(R.id.employee_imageView);
            isCritical_imageView = itemView.findViewById(R.id.isCritical_imageView);

            // ConstrainLayout
            constraintLayout = itemView.findViewById(R.id.violation_row_layout);
        }
    }
}
