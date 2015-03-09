/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.ucsd.ccdb.segmentation.chm.rest;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
/**
 * This class is the model class for storing the metadata of the CHM model.
 * 
 * @author Willy W. Wong
 */
@XmlRootElement(name = "CHM_Model")
public class CHM_Model 
{
    private long id = -1;
    private long MPID = -1;
    private String DESCRIPTION = null;
    private String MODEL_NAME = null;

    private String acquisition_time;
    private String species = null;
    private String gender = null;
    private double age = -1;
    private String microscope = null;
    private int magnification = -1;
    private double pixel_size_in_nm=-1;
    private double step_size_in_nm =-1;
    private int dimension_x = -1;
    private int dimension_y = -1;
    private int dimension_z = -1;
    private double acc_voltage_in_kv=-1;
    private double pressure_torr=-1;
    private int dwell_time_ns = -1;
    private double knife_cut_speed_mm =-1;
    private double knife_retract_speed_mm=-1;
    private double spot_size=-1;
    private String training_target = null;
    private int training_x=-1;
    private int training_y=-1;
    private int training_z=-1;
    private double training_pixel_size_nm=-1;
    private int training_downsample_factor=-1;
    private int training_stages=-1;
    private int training_levels=-1;
    private String anatomy=null;
    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    //@XmlAttribute
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @return the MPID
     */
    public long getMPID() {
        return MPID;
    }

    /**
     * @param MPID the MPID to set
     */
    //@XmlAttribute
    public void setMPID(long MPID) {
        this.MPID = MPID;
    }

    /**
     * @return the DESCRIPTION
     */
    public String getDESCRIPTION() {
        return DESCRIPTION;
    }

    /**
     * @param DESCRIPTION the DESCRIPTION to set
     */
    //@XmlAttribute
    public void setDESCRIPTION(String DESCRIPTION) {
        this.DESCRIPTION = DESCRIPTION;
    }

    /**
     * @return the MODEL_NAME
     */
    public String getMODEL_NAME() {
        return MODEL_NAME;
    }

    /**
     * @param MODEL_NAME the MODEL_NAME to set
     */
    //@XmlAttribute
    public void setMODEL_NAME(String MODEL_NAME) {
        this.MODEL_NAME = MODEL_NAME;
    }

    /**
     * @return the acquisition_time
     */
    public String getAcquisition_time() {
        return acquisition_time;
    }

    /**
     * @param acquisition_time the acquisition_time to set
     */
    public void setAcquisition_time(String acquisition_time) {
        this.acquisition_time = acquisition_time;
    }

    /**
     * @return the species
     */
    public String getSpecies() {
        return species;
    }

    /**
     * @param species the species to set
     */
    public void setSpecies(String species) {
        this.species = species;
    }

    /**
     * @return the gender
     */
    public String getGender() {
        return gender;
    }

    /**
     * @param gender the gender to set
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * @return the age
     */
    public double getAge() {
        return age;
    }

    /**
     * @param age the age to set
     */
    public void setAge(double age) {
        this.age = age;
    }

    /**
     * @return the microscope
     */
    public String getMicroscope() {
        return microscope;
    }

    /**
     * @param microscope the microscope to set
     */
    public void setMicroscope(String microscope) {
        this.microscope = microscope;
    }

    /**
     * @return the magnification
     */
    public int getMagnification() {
        return magnification;
    }

    /**
     * @param magnification the magnification to set
     */
    public void setMagnification(int magnification) {
        this.magnification = magnification;
    }

    /**
     * @return the pxiel_size_in_nm
     */
    public double getPixel_size_in_nm() {
        return pixel_size_in_nm;
    }

    /**
     * @param pixel_size_in_nm the pxiel_size_in_nm to set
     */
    public void setPixel_size_in_nm(double pixel_size_in_nm) {
        this.pixel_size_in_nm = pixel_size_in_nm;
    }

    /**
     * @return the step_size_in_nm
     */
    public double getStep_size_in_nm() {
        return step_size_in_nm;
    }

    /**
     * @param step_size_in_nm the step_size_in_nm to set
     */
    public void setStep_size_in_nm(double step_size_in_nm) {
        this.step_size_in_nm = step_size_in_nm;
    }

    /**
     * @return the dimension_x
     */
    public int getDimension_x() {
        return dimension_x;
    }

    /**
     * @param dimension_x the dimension_x to set
     */
    public void setDimension_x(int dimension_x) {
        this.dimension_x = dimension_x;
    }

    /**
     * @return the dimension_y
     */
    public int getDimension_y() {
        return dimension_y;
    }

    /**
     * @param dimension_y the dimension_y to set
     */
    public void setDimension_y(int dimension_y) {
        this.dimension_y = dimension_y;
    }

    /**
     * @return the dimension_z
     */
    public int getDimension_z() {
        return dimension_z;
    }

    /**
     * @param dimension_z the dimension_z to set
     */
    public void setDimension_z(int dimension_z) {
        this.dimension_z = dimension_z;
    }

    /**
     * @return the acc_voltage_in_kv
     */
    public double getAcc_voltage_in_kv() {
        return acc_voltage_in_kv;
    }

    /**
     * @param acc_voltage_in_kv the acc_voltage_in_kv to set
     */
    public void setAcc_voltage_in_kv(double acc_voltage_in_kv) {
        this.acc_voltage_in_kv = acc_voltage_in_kv;
    }

    /**
     * @return the pressure_torr
     */
    public double getPressure_torr() {
        return pressure_torr;
    }

    /**
     * @param pressure_torr the pressure_torr to set
     */
    public void setPressure_torr(double pressure_torr) {
        this.pressure_torr = pressure_torr;
    }

    /**
     * @return the dwell_time_ns
     */
    public int getDwell_time_ns() {
        return dwell_time_ns;
    }

    /**
     * @param dwell_time_ns the dwell_time_ns to set
     */
    public void setDwell_time_ns(int dwell_time_ns) {
        this.dwell_time_ns = dwell_time_ns;
    }

    /**
     * @return the knife_cut_speed_mm
     */
    public double getKnife_cut_speed_mm() {
        return knife_cut_speed_mm;
    }

    /**
     * @param knife_cut_speed_mm the knife_cut_speed_mm to set
     */
    public void setKnife_cut_speed_mm(double knife_cut_speed_mm) {
        this.knife_cut_speed_mm = knife_cut_speed_mm;
    }

    /**
     * @return the knife_retract_speed_mm
     */
    public double getKnife_retract_speed_mm() {
        return knife_retract_speed_mm;
    }

    /**
     * @param knife_retract_speed_mm the knife_retract_speed_mm to set
     */
    public void setKnife_retract_speed_mm(double knife_retract_speed_mm) {
        this.knife_retract_speed_mm = knife_retract_speed_mm;
    }

    /**
     * @return the spot_size
     */
    public double getSpot_size() {
        return spot_size;
    }

    /**
     * @param spot_size the spot_size to set
     */
    public void setSpot_size(double spot_size) {
        this.spot_size = spot_size;
    }

    /**
     * @return the training_target
     */
    public String getTraining_target() {
        return training_target;
    }

    /**
     * @param training_target the training_target to set
     */
    public void setTraining_target(String training_target) {
        this.training_target = training_target;
    }

    /**
     * @return the training_x
     */
    public int getTraining_x() {
        return training_x;
    }

    /**
     * @param training_x the training_x to set
     */
    public void setTraining_x(int training_x) {
        this.training_x = training_x;
    }

    /**
     * @return the training_y
     */
    public int getTraining_y() {
        return training_y;
    }

    /**
     * @param training_y the training_y to set
     */
    public void setTraining_y(int training_y) {
        this.training_y = training_y;
    }

    /**
     * @return the training_z
     */
    public int getTraining_z() {
        return training_z;
    }

    /**
     * @param training_z the training_z to set
     */
    public void setTraining_z(int training_z) {
        this.training_z = training_z;
    }

    /**
     * @return the training_pixel_size_nm
     */
    public double getTraining_pixel_size_nm() {
        return training_pixel_size_nm;
    }

    /**
     * @param training_pixel_size_nm the training_pixel_size_nm to set
     */
    public void setTraining_pixel_size_nm(double training_pixel_size_nm) {
        this.training_pixel_size_nm = training_pixel_size_nm;
    }

    /**
     * @return the training_downsample_factor
     */
    public int getTraining_downsample_factor() {
        return training_downsample_factor;
    }

    /**
     * @param training_downsample_factor the training_downsample_factor to set
     */
    public void setTraining_downsample_factor(int training_downsample_factor) {
        this.training_downsample_factor = training_downsample_factor;
    }

    /**
     * @return the training_stages
     */
    public int getTraining_stages() {
        return training_stages;
    }

    /**
     * @param training_stages the training_stages to set
     */
    public void setTraining_stages(int training_stages) {
        this.training_stages = training_stages;
    }

    /**
     * @return the training_levels
     */
    public int getTraining_levels() {
        return training_levels;
    }

    /**
     * @param training_levels the training_levels to set
     */
    public void setTraining_levels(int training_levels) {
        this.training_levels = training_levels;
    }

    /**
     * @return the anatomy
     */
    public String getAnatomy() {
        return anatomy;
    }

    /**
     * @param anatomy the anatomy to set
     */
    public void setAnatomy(String anatomy) {
        this.anatomy = anatomy;
    }
    
}
