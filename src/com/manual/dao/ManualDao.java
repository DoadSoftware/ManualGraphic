package com.manual.dao;

import java.util.List;

import com.manual.model.Match;
import com.manual.model.Player;
import com.manual.model.Team;

public interface ManualDao {
  Player getPlayer(int player_id);
  Team getTeam(int team_id);
  List<Match> getAllMatches();
  Match getMatch(int matchID);
}