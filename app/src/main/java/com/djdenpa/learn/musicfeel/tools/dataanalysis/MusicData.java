package com.djdenpa.learn.musicfeel.tools.dataanalysis;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by denpa on 4/12/2017.
 *
 * This class is for holding all relevant analysis we can pull from a song.
 *
 * I plan for it to have a bunch of fields to hold data
 *
 * Then it has a "State" where It knows step by step what process to run next.
 *
 * current rough plan
 * First run through and get an array of every significant spectral flux spike
 * Then run an algorithm on the spikes to get a rough bpm.
 * Possibly use rough bpm to optimize FFT runs to get an even more accurate bpm.
 * Then use that to start detecting music signals to generate notes.
 * Then start a process to
 *
 */

public class MusicData {
  private UUID _id = UUID.randomUUID();
  public UUID id(){
    return _id;
  }
  private DataState state = DataState.GetSpectralFluxSpikes;
  enum DataState{GetSpectralFluxSpikes}

  private ArrayList<Long> spectralFluxSpikes;

}
