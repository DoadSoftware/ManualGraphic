package com.manual.dao.impl;

import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.manual.dao.ManualDao;
import com.manual.model.Match;
import com.manual.model.Player;
import com.manual.model.Team;

@Transactional
@Repository("manualDao")
@SuppressWarnings("unchecked")
public class ManualDaoImpl implements ManualDao {

 @Autowired
 private SessionFactory sessionFactory;
 
@Override
public Player getPlayer(int player_id) {
	return (Player) sessionFactory.getCurrentSession().createQuery("from Player WHERE PlayerId=" + player_id).uniqueResult();  
}

@Override
public Team getTeam(int team_id) {
	return (Team) sessionFactory.getCurrentSession().createQuery("from Team WHERE TeamId=" + team_id).uniqueResult();  
}

@Override
public List<Match> getAllMatches() {
	return sessionFactory.getCurrentSession().createQuery("from Match").list();
}

@Override
public Match getMatch(int matchID) {
	return (Match) sessionFactory.getCurrentSession().createQuery("from Match WHERE matchId = " + String.valueOf(matchID)).uniqueResult();
}

}