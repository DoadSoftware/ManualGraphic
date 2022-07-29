package com.manual.dao.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.manual.dao.ManualDao;

@Transactional
@Repository("manualDao")
public class ManualDaoImpl implements ManualDao {

 @SuppressWarnings("unused")
@Autowired
 private SessionFactory sessionFactory;

}