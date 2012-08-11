/*
 * This file is part of Rootbeer.
 * 
 * Rootbeer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Rootbeer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Rootbeer.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package edu.syr.pcpratts.rootbeer.testcases.rootbeertest.ofcoarse;

public class CoarseningPhysics {

  double mCnum;
  double mVolumeScale;
  double mTimeStep;
  double mAgeStep;
  double mCurrAge;
  int mBorder;
  VariableAgeCutoff m_VariableAgeStep;
  boolean m_UsingVariableAgeStep;

  double mMinAggregateSize;
  GpuList<Droplet> mPrevDroplets;
  GpuList<GpuNumber> mDebug;

  public CoarseningPhysics(){
    mMinAggregateSize = 200;
    mCnum = 2.8;
    mVolumeScale = 0.9;
    mTimeStep = 800;
    mAgeStep = 1;
    m_UsingVariableAgeStep = false;
    mDebug = new GpuList<GpuNumber>();
  }
  
  public GpuList<GpuNumber> getDebug(){
    return mDebug;
  }
  
  public GpuList<Droplet> makePrediction(GpuList<Droplet> droplets) {
    mPrevDroplets = new GpuList<Droplet>();
    for(int i = 0; i < droplets.size(); ++i){
      Droplet drop = droplets.get(i);
      mPrevDroplets.add(new Droplet(drop));
    }
    resetDropletAverages();
    doPairDropletPrediction();
    averageValues();
    removeSmallDroplets();
    return mergeDroplets();
  }

  public void setParameters(double cnum, double volumescale, double timestep,
    double age_step, int border){

    mCnum = cnum;
    mVolumeScale = volumescale;
    mTimeStep = timestep;
    mAgeStep = age_step;
    mBorder = border;
    m_UsingVariableAgeStep = false;
  }  
  
  public void setParameters(double cnum, double volumescale, double timestep,
    int border, VariableAgeCutoff age_step){

    mCnum = cnum;
    mVolumeScale = volumescale;
    mTimeStep = timestep;
    m_VariableAgeStep = age_step;
    mBorder = border;
    m_UsingVariableAgeStep = true;
  }

  public void setCurrAge(double curr_age){
    mCurrAge = curr_age;
  }

  double volume_update_drop1(double v1_k, double v2_k, Point x1_k, Point x2_k){
    double L_k = PointDistance.distance(x1_k, x2_k);
    v1_k *= mVolumeScale;
    v2_k *= mVolumeScale;
		double term1 = v1_k;
		double term2 = mTimeStep * (4 * StrictMath.pow(StrictMath.PI, 4.0/3.0)) / (StrictMath.log(L_k / StrictMath.pow(v1_k, 1.0/3.0)) +
		                                                           StrictMath.log(L_k / StrictMath.pow(v2_k, 1.0/3.0))) *
						(StrictMath.pow(v1_k, -1.0/3.0) - StrictMath.pow(v2_k, -1.0/3.0));
		return term1 - term2;
	}

	double volume_update_drop2(double v1_k, double v2_k, double v1_k_plus_1){
    v1_k *= mVolumeScale;
    v2_k *= mVolumeScale;
    return v2_k - (v1_k_plus_1 - v1_k);
	}
  
  double log10(double value){
    return StrictMath.log(value) / StrictMath.log(10.0);
  }

  Point center_update_drop1(Point x1_k, Point x2_k, double v1_k, double v1_k_plus_1){
    v1_k *= mVolumeScale;
		double g_1 = 2.0 / StrictMath.pow(StrictMath.PI, 2.0/3.0) * mCnum * StrictMath.pow(v1_k, 2.0/3.0);
		double L_12 = PointDistance.distance(x1_k, x2_k);
		double c_12 = - 1 / (3 * StrictMath.pow(StrictMath.PI, 1.0/3.0)) * StrictMath.pow(L_12, -1) * StrictMath.pow(v1_k, 1.0/3.0) *
			log10(v1_k);

		double x1_k_plus_1_x = x1_k.X - ((c_12 / g_1)*(v1_k_plus_1 - v1_k));
		double x1_k_plus_1_y = x1_k.Y - ((c_12 / g_1)*(v1_k_plus_1 - v1_k));
		return new Point(x1_k_plus_1_x, x1_k_plus_1_y);
	}

	Point center_update_drop2(Point  x1_k, Point  x2_k, double v1_k, double v1_k_plus_1, double v2_k){
    v1_k *= mVolumeScale;
    v2_k *= mVolumeScale;
		double g_2 = 2.0 / StrictMath.pow(StrictMath.PI, 2.0/3.0) * mCnum * StrictMath.pow(v2_k, 2.0/3.0);
		double L_21 = PointDistance.distance(x2_k, x1_k);
		double c_21 = - 1 / (3 * StrictMath.pow(StrictMath.PI, 1.0/3.0)) * StrictMath.pow(L_21, -1) * StrictMath.pow(v2_k, 1.0/3.0) *
			log10(v2_k);

		double x2_k_plus_1_x = x2_k.X - ((c_21 / g_2)*(v1_k_plus_1 - v1_k));
		double x2_k_plus_1_y = x2_k.Y - ((c_21 / g_2)*(v1_k_plus_1 - v1_k));
		return new Point(x2_k_plus_1_x, x2_k_plus_1_y);
	}

  private void averageValues() {
    for(int i = 0; i < mPrevDroplets.size(); ++i){
      Droplet drop = mPrevDroplets.get(i);
      drop.finalizePrediction();
      if(drop.getVolume() < 0.000000001){
        mPrevDroplets.remove(i);
        --i;
      }
    }
  }

  private void doPairDropletPrediction() {
    for(int i = 0; i < mPrevDroplets.size(); ++i){
      Droplet drop1 = mPrevDroplets.get(i);
      
      if(drop1.getVolume() < 0.000000001)
        continue;
      
      for(int j = i + 1; j < mPrevDroplets.size(); ++j){
        Droplet drop2 = mPrevDroplets.get(j);        
        
        if(drop2.getVolume() < 0.000000001)
          continue;      
        
        double drop1_volume;
        double drop2_volume;
        
        if(pastAgeStep(drop1.getCenter(), drop2.getCenter())){
          drop1_volume = drop1.getVolume();
          drop2_volume = drop2.getVolume();        
        } else {
          drop1_volume = volume_update_drop1(drop1.getVolume(), drop2.getVolume(), drop1.getCenter(),
                drop2.getCenter());
          drop2_volume = volume_update_drop2(drop1.getVolume(), drop2.getVolume(), drop1_volume);
        }
        if(drop1_volume <= 0.000000001){
          continue; 
        }        
        
        if(drop2_volume <= 0.000000001){
          continue; 
        }

        if(dropletsTooClose(drop1.getCenter(), drop2.getCenter())){
          continue;
        }

        Point drop1_center = center_update_drop1(drop1.getCenter(), drop2.getCenter(), drop1.getVolume(),
                drop1_volume);
        Point drop2_center = center_update_drop2(drop1.getCenter(), drop2.getCenter(), drop1.getVolume(),
                drop1_volume, drop2.getVolume());

        drop1.addNextData(drop1_center, drop1_volume);
        drop2.addNextData(drop2_center, drop2_volume);
      }
    }
  }

  public Point midpoint(Point p1, Point p2){
    double x = (p1.X + p2.X) / 2.0;
    double y = (p1.Y + p2.Y) / 2.0;
    return new Point(x, y);
  }

  private GpuList<Droplet> mergeDroplets() {
    GpuList<Droplet> output = new GpuList<Droplet>();
    for(int i = 0; i < mPrevDroplets.size(); ++i){
      Droplet drop1 = mPrevDroplets.get(i);
      for(int j = i + 1; j < mPrevDroplets.size(); ++j){
        Droplet drop2 = mPrevDroplets.get(j);
        double distance = PointDistance.distance(drop1.getCenter(), drop2.getCenter());
        double radius1 = drop1.getRadius();
        double radius2 = drop2.getRadius();
        if(distance - (radius1 + radius2) <= 0){
          Point center = midpoint(drop1.getCenter(), drop2.getCenter());
          double volume = drop1.getVolume() + drop2.getVolume();
          drop1.resetNextData();
          drop1.addNextData(center, volume);
          drop1.finalizePrediction();
          mPrevDroplets.remove(j);
          break;
        }
      }
      output.add(new Droplet(drop1));
    }

    return output;
  }

  private void removeSmallDroplets() {
    for(int i = 0; i < mPrevDroplets.size(); ++i){
      Droplet curr = mPrevDroplets.get(i);
      if(curr.getVolume() < mMinAggregateSize){
        mPrevDroplets.remove(i);
        --i;
      }
    }
  }

  private void resetDropletAverages() {
    for(int i = 0; i < mPrevDroplets.size(); ++i)
      mPrevDroplets.get(i).resetNextData();
  }

  private boolean dropletsTooClose(Point center1, Point center2) {
    if(PointDistance.distance(center1, center2) < 0.00000001)
      return true;
    return false;
  }

  private boolean pastAgeStep(Point x1_k, Point x2_k) {
    if(m_UsingVariableAgeStep == false){
      if(mCurrAge > mAgeStep)
        return true;
      return false;
    } 
    
    if(m_VariableAgeStep.pastAgeStep(x1_k, mCurrAge))
      return true;
    if(m_VariableAgeStep.pastAgeStep(x2_k, mCurrAge))
      return true;
    return false;
  }

}
