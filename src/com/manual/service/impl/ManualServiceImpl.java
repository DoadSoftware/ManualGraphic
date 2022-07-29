package com.manual.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.manual.dao.ManualDao;
import com.manual.service.ManualService;

@Service("manualService")
@Transactional
public class ManualServiceImpl implements ManualService {

 @SuppressWarnings("unused")
@Autowired
 private ManualDao manualDao;
 
}