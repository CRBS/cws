/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ucsd.ccdb.segmentation.chm.rest;
import java.util.*;
import java.sql.*;
/**
 * DBUtil is the class for retrieving data from the database.
 * 
 * @author Willy W. Wong
 */
public class DBUtil 
{
    DBService db =new DBService();
    
    /**
     * This method retrieve the metadata of the CHM model from the database.
     * Then it wraps the data into a List of CHM_Model objects.
     * 
     * @return
     * @throws Exception 
     */
    public List<CHM_Model> getAllChmModelsWithDetails() throws Exception
    {
        List<CHM_Model> list = new ArrayList<CHM_Model>();
        String sql = "select id, mpid, description, model_name,\n" +
"acquisition_date, species, gender, age_in_month,\n" +
"microscope, magnification, pixel_size_in_nm, step_size_in_nm, \n" +
"dimension_x, dimension_y, dimension_z, accelerating_voltage_kv,\n" +
"pressure_torr, dwell_time_ns, knife_cut_speed_mm_per_s, spot_size,\n" +
"training_target, training_dim_x, training_dim_y, training_dim_z,\n" +
"training_pixel_size_nm, training_downsample_factor, training_stages, \n" +
"training_levels, anatomy, knife_retract_speed_mm_per_s from slash_chm_model order by id asc";        
    
        Connection c = db.getConnection();
        PreparedStatement ps = c.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while(rs.next())
        {
            CHM_Model model = new CHM_Model();
            long id = rs.getLong("id");
            long mpid = rs.getLong("mpid");
            String desc = rs.getString("description");
            String modelName = rs.getString("model_name");
            java.sql.Timestamp acqDate = rs.getTimestamp("acquisition_date");
            String species = rs.getString("species");
            String gender = rs.getString("gender");
            double age = rs.getDouble("age_in_month");
            String microscope = rs.getString("microscope");
            int magnification = rs.getInt("magnification");
            double pixel_size_in_nm = rs.getDouble("pixel_size_in_nm");
            double step_size_in_nm = rs.getDouble("step_size_in_nm");
            int dimension_x = rs.getInt("dimension_x");
            int dimension_y = rs.getInt("dimension_y");
            int dimension_z = rs.getInt("dimension_z");
            double accelerating_voltage_kv = rs.getDouble("accelerating_voltage_kv");
            double pressure_torr = rs.getDouble("pressure_torr");
            int dwell_time_ns = rs.getInt("dwell_time_ns");
            double knife_cut_speed_mm_per_s = rs.getDouble("knife_cut_speed_mm_per_s");
            double spot_size = rs.getDouble("spot_size");
            String training_target = rs.getString("training_target");
            int training_dim_x = rs.getInt("training_dim_x");
            int training_dim_y = rs.getInt("training_dim_y");
            int training_dim_z = rs.getInt("training_dim_z");
            double training_pixel_size_nm = rs.getDouble("training_pixel_size_nm");
            int training_downsample_factor = rs.getInt("training_downsample_factor");
            int training_stages = rs.getInt("training_stages");
            int training_levels = rs.getInt("training_levels");
            String anatomy = rs.getString("anatomy");
            double knife_retract_speed_mm_per_s = rs.getDouble("knife_retract_speed_mm_per_s");
            
            
            model.setId(id);
            model.setMPID(mpid);
            model.setDESCRIPTION(desc);
            model.setMODEL_NAME(modelName);
            model.setAcquisition_time(acqDate.toString());
            model.setSpecies(species);
            model.setAge(age);
            model.setMicroscope(microscope);
            model.setMagnification(magnification);
            model.setPixel_size_in_nm(pixel_size_in_nm);
            model.setDimension_x(dimension_x);
            model.setDimension_y(dimension_y);
            model.setDimension_z(dimension_z);
            model.setAcc_voltage_in_kv(accelerating_voltage_kv);
            model.setDwell_time_ns(dwell_time_ns);
            model.setPressure_torr(pressure_torr);
            model.setKnife_cut_speed_mm(knife_cut_speed_mm_per_s);
            model.setSpot_size(spot_size);
            model.setStep_size_in_nm(step_size_in_nm);
            //System.err.println("Spot Size:"+model.getSpot_size());
            
            model.setTraining_target(training_target);
            model.setTraining_x(training_dim_x);
            model.setTraining_y(training_dim_y);
            model.setTraining_z(training_dim_z);
            model.setTraining_pixel_size_nm(training_pixel_size_nm);
            model.setTraining_downsample_factor(training_downsample_factor);
            model.setTraining_stages(training_stages);
            model.setTraining_levels(training_levels);
            model.setAnatomy(anatomy);
            model.setKnife_retract_speed_mm(knife_retract_speed_mm_per_s);
            list.add(model);
            
        }
        c.close();
        return list;
    
    
    }
    
    
    /*public List<CHM_Model> getAllChmModels()throws Exception
    {
        List<CHM_Model> list = new ArrayList<CHM_Model>();
        String sql = "select id, mpid, description, model_name from slash_chm_model";
        Connection c = db.getConnection();
        PreparedStatement ps = c.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while(rs.next())
        {
            CHM_Model model = new CHM_Model();
            long id = rs.getLong("id");
            long mpid = rs.getLong("mpid");
            String desc = rs.getString("description");
            String modelName = rs.getString("model_name");
            model.setId(id);
            model.setMPID(mpid);
            model.setDESCRIPTION(desc);
            model.setMODEL_NAME(modelName);
            list.add(model);
            
        }
        c.close();
        return list;
    }*/
    
    
    /**
     * This method retrieves the metadata of the individual CHM model. The input parameter,
     * id, specifies which CHM model to be retrieved.
     * 
     * @param id
     * @return
     * @throws Exception 
     */
    public CHM_Model getChmModel(long id) throws Exception
    {
        
          String sql = "select id, mpid, description, model_name,\n" +
"acquisition_date, species, gender, age_in_month,\n" +
"microscope, magnification, pixel_size_in_nm, step_size_in_nm, \n" +
"dimension_x, dimension_y, dimension_z, accelerating_voltage_kv,\n" +
"pressure_torr, dwell_time_ns, knife_cut_speed_mm_per_s, spot_size,\n" +
"training_target, training_dim_x, training_dim_y, training_dim_z,\n" +
"training_pixel_size_nm, training_downsample_factor, training_stages, \n" +
"training_levels, anatomy, knife_retract_speed_mm_per_s from slash_chm_model where id = ?";        
    
        Connection c = db.getConnection();
        PreparedStatement ps = c.prepareStatement(sql);
        ps.setLong(1, id);
        ResultSet rs = ps.executeQuery();
        CHM_Model model = null;
        if(rs.next())
        {
            
            
            model = new CHM_Model();
           
            long mpid = rs.getLong("mpid");
            String desc = rs.getString("description");
            String modelName = rs.getString("model_name");
            java.sql.Timestamp acqDate = rs.getTimestamp("acquisition_date");
            String species = rs.getString("species");
            String gender = rs.getString("gender");
            double age = rs.getDouble("age_in_month");
            String microscope = rs.getString("microscope");
            int magnification = rs.getInt("magnification");
            double pixel_size_in_nm = rs.getDouble("pixel_size_in_nm");
            double step_size_in_nm = rs.getDouble("step_size_in_nm");
            int dimension_x = rs.getInt("dimension_x");
            int dimension_y = rs.getInt("dimension_y");
            int dimension_z = rs.getInt("dimension_z");
            double accelerating_voltage_kv = rs.getDouble("accelerating_voltage_kv");
            double pressure_torr = rs.getDouble("pressure_torr");
            int dwell_time_ns = rs.getInt("dwell_time_ns");
            double knife_cut_speed_mm_per_s = rs.getDouble("knife_cut_speed_mm_per_s");
            double spot_size = rs.getDouble("spot_size");
            String training_target = rs.getString("training_target");
            int training_dim_x = rs.getInt("training_dim_x");
            int training_dim_y = rs.getInt("training_dim_y");
            int training_dim_z = rs.getInt("training_dim_z");
            double training_pixel_size_nm = rs.getDouble("training_pixel_size_nm");
            int training_downsample_factor = rs.getInt("training_downsample_factor");
            int training_stages = rs.getInt("training_stages");
            int training_levels = rs.getInt("training_levels");
            String anatomy = rs.getString("anatomy");
            double knife_retract_speed_mm_per_s = rs.getDouble("knife_retract_speed_mm_per_s");
            
            
            model.setId(id);
            model.setMPID(mpid);
            model.setDESCRIPTION(desc);
            model.setMODEL_NAME(modelName);
            model.setAcquisition_time(acqDate.toString());
            model.setSpecies(species);
            model.setAge(age);
            model.setMicroscope(microscope);
            model.setMagnification(magnification);
            model.setPixel_size_in_nm(pixel_size_in_nm);
            model.setDimension_x(dimension_x);
            model.setDimension_y(dimension_y);
            model.setDimension_z(dimension_z);
            model.setAcc_voltage_in_kv(accelerating_voltage_kv);
            model.setDwell_time_ns(dwell_time_ns);
            model.setPressure_torr(pressure_torr);
            model.setKnife_cut_speed_mm(knife_cut_speed_mm_per_s);
            model.setSpot_size(spot_size);
            model.setStep_size_in_nm(step_size_in_nm);
            //System.err.println("Spot Size:"+model.getSpot_size());
            
            model.setTraining_target(training_target);
            model.setTraining_x(training_dim_x);
            model.setTraining_y(training_dim_y);
            model.setTraining_z(training_dim_z);
            model.setTraining_pixel_size_nm(training_pixel_size_nm);
            model.setTraining_downsample_factor(training_downsample_factor);
            model.setTraining_stages(training_stages);
            model.setTraining_levels(training_levels);
            model.setAnatomy(anatomy);
            model.setKnife_retract_speed_mm(knife_retract_speed_mm_per_s);
        }
        c.close();
        return model;
        
    }
    
    
    
}
