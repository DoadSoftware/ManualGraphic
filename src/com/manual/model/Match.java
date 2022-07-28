package com.manual.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Column;

@Entity
@Table(name = "Match")
public class Match {

  @Id
  @Column(name = "matchId")
  private int matchId;
	
  @Column(name = "homePlayerId")
  private int homePlayerId;

  @Column(name = "awayPlayerId")
  private int awayPlayerId;

  @Column(name = "numberOfFrames")
  private int numberOfFrames;
  
  @Transient
  private Player homePlayer;

  @Transient
  private Player awayPlayer;

public Match(int matchId) {
	super();
	this.matchId = matchId;
}

public Match() {
	super();
}

public int getMatchId() {
	return matchId;
}

public void setMatchId(int matchId) {
	this.matchId = matchId;
}

public int getHomePlayerId() {
	return homePlayerId;
}

public void setHomePlayerId(int homePlayerId) {
	this.homePlayerId = homePlayerId;
}

public int getAwayPlayerId() {
	return awayPlayerId;
}

public void setAwayPlayerId(int awayPlayerId) {
	this.awayPlayerId = awayPlayerId;
}

public int getNumberOfFrames() {
	return numberOfFrames;
}

public void setNumberOfFrames(int numberOfFrames) {
	this.numberOfFrames = numberOfFrames;
}

public Player getHomePlayer() {
	return homePlayer;
}

public void setHomePlayer(Player homePlayer) {
	this.homePlayer = homePlayer;
}

public Player getAwayPlayer() {
	return awayPlayer;
}

public void setAwayPlayer(Player awayPlayer) {
	this.awayPlayer = awayPlayer;
}

@Override
public String toString() {
	return "Match [matchId=" + matchId + ", homePlayerId=" + homePlayerId + ", awayPlayerId=" + awayPlayerId
			+ ", numberOfFrames=" + numberOfFrames + ", homePlayer=" + homePlayer + ", awayPlayer=" + awayPlayer + "]";
}
  
}