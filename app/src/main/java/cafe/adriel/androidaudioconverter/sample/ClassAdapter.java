package cafe.adriel.androidaudioconverter.sample;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ViewHolder>  {

    List<Class> classes;


    public ClassAdapter(List<Class> classes) {
        this.classes = classes;
    }
    //Date currentTime = Calendar.getInstance().HOUR_OF_DAY;
    static String match_name="default";



    SimpleDateFormat formatter = new SimpleDateFormat("HH");
    Date today=Calendar.getInstance().getTime();
    String temp=formatter.format(today);

    public ClassAdapter(){}
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.class_row,parent,false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder (@NonNull ClassAdapter.ViewHolder viewHolder, int i) {

        viewHolder.classname.setText("수업명: "+classes.get(i).getClassname());
        viewHolder.professorname.setText("교수님: "+classes.get(i).getProfessorname());
        viewHolder.starttime.setText("녹음시작: "+classes.get(i).getStarttime());
        viewHolder.recordday.setText("수업요일: "+classes.get(i).getRecordday());


        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("yyMMdd");
        SimpleDateFormat mdformat2 = new SimpleDateFormat("HH:mm");




        Log.d("시간표시간",classes.get(i).getStarttime().toString());
        Log.d("현재시간= ",mdformat2.format(calendar.getTime()).toString());
        //match_name=mdformat.format(calendar.getTime())+"_"+classes.get(i).getClassname()+"_"+classes.get(i).getProfessorname();

        Log.d("시간 matchname= ",match_name);

    }



    @Override
    public int getItemCount() {
        return classes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView classname;
        public TextView professorname;
        public TextView starttime;
        public TextView recordday;


        public ViewHolder(View itemView){
            super(itemView);
            classname=itemView.findViewById(R.id.class_name);
            professorname=itemView.findViewById(R.id.professor_name);
            starttime=itemView.findViewById(R.id.start_time);
            recordday=itemView.findViewById(R.id.record_day);


        }
    }
}
