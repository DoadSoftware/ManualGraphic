package com.manual.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.manual.dao.ManualDao;
import com.manual.model.Match;
import com.manual.model.Player;
import com.manual.model.Team;
import com.manual.service.ManualService;

@Service("manualService")
@Transactional
public class ManualServiceImpl implements ManualService {

 @Autowired
 private ManualDao manualDao;
 
@Override
public Player getPlayer(int player_id) {
	return manualDao.getPlayer(player_id);
}

@Override
public Team getTeam(int team_id) {
	return manualDao.getTeam(team_id);
}

@Override
public List<Match> getAllMatches() {
	return manualDao.getAllMatches();
}

@Override
public Match getMatch(int matchID) {
	return manualDao.getMatch(matchID);
}

}