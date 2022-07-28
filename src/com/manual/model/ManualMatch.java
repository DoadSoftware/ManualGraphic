package com.manual.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.List;

@XmlRootElement(name="match")
@XmlAccessorType(XmlAccessType.FIELD)
public class ManualMatch {

  @XmlElement
  private Match match;
	
  @XmlElementWrapper(name = "stats")
  @XmlElement(name = "stat")
  private List<Stats> stats;

  @XmlTransient
  private String match_file_timestamp;

  @XmlTransient
  private List<Match> matches;
  
public ManualMatch(Match match) {
	super();
	this.match = match;
}

public ManualMatch() {
	super();
}

public List<Match> getMatches() {
	return matches;
}

public void setMatches(List<Match> matches) {
	this.matches = matches;
}

public Match getMatch() {
	return match;
}

public void setMatch(Match match) {
	this.match = match;
}

public List<Stats> getStats() {
	return stats;
}

public void setStats(List<Stats> stats) {
	this.stats = stats;
}

public String getMatch_file_timestamp() {
	return match_file_timestamp;
}

public void setMatch_file_timestamp(String match_file_timestamp) {
	this.match_file_timestamp = match_file_timestamp;
}

}