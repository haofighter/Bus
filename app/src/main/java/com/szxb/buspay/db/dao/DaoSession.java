package com.szxb.buspay.db.dao;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.szxb.buspay.db.entity.bean.card.ConsumeCard;
import com.szxb.buspay.db.entity.card.BlackListCard;
import com.szxb.buspay.db.entity.card.CardRecord;
import com.szxb.buspay.db.entity.card.LineInfoEntity;
import com.szxb.buspay.db.entity.scan.BlackListEntity;
import com.szxb.buspay.db.entity.scan.MacKeyEntity;
import com.szxb.buspay.db.entity.scan.PublicKeyEntity;
import com.szxb.buspay.db.entity.scan.ScanInfoEntity;
import com.szxb.unionpay.entity.UnionAidEntity;
import com.szxb.unionpay.entity.UnionPayEntity;

import com.szxb.buspay.db.dao.ConsumeCardDao;
import com.szxb.buspay.db.dao.BlackListCardDao;
import com.szxb.buspay.db.dao.CardRecordDao;
import com.szxb.buspay.db.dao.LineInfoEntityDao;
import com.szxb.buspay.db.dao.BlackListEntityDao;
import com.szxb.buspay.db.dao.MacKeyEntityDao;
import com.szxb.buspay.db.dao.PublicKeyEntityDao;
import com.szxb.buspay.db.dao.ScanInfoEntityDao;
import com.szxb.buspay.db.dao.UnionAidEntityDao;
import com.szxb.buspay.db.dao.UnionPayEntityDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig consumeCardDaoConfig;
    private final DaoConfig blackListCardDaoConfig;
    private final DaoConfig cardRecordDaoConfig;
    private final DaoConfig lineInfoEntityDaoConfig;
    private final DaoConfig blackListEntityDaoConfig;
    private final DaoConfig macKeyEntityDaoConfig;
    private final DaoConfig publicKeyEntityDaoConfig;
    private final DaoConfig scanInfoEntityDaoConfig;
    private final DaoConfig unionAidEntityDaoConfig;
    private final DaoConfig unionPayEntityDaoConfig;

    private final ConsumeCardDao consumeCardDao;
    private final BlackListCardDao blackListCardDao;
    private final CardRecordDao cardRecordDao;
    private final LineInfoEntityDao lineInfoEntityDao;
    private final BlackListEntityDao blackListEntityDao;
    private final MacKeyEntityDao macKeyEntityDao;
    private final PublicKeyEntityDao publicKeyEntityDao;
    private final ScanInfoEntityDao scanInfoEntityDao;
    private final UnionAidEntityDao unionAidEntityDao;
    private final UnionPayEntityDao unionPayEntityDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        consumeCardDaoConfig = daoConfigMap.get(ConsumeCardDao.class).clone();
        consumeCardDaoConfig.initIdentityScope(type);

        blackListCardDaoConfig = daoConfigMap.get(BlackListCardDao.class).clone();
        blackListCardDaoConfig.initIdentityScope(type);

        cardRecordDaoConfig = daoConfigMap.get(CardRecordDao.class).clone();
        cardRecordDaoConfig.initIdentityScope(type);

        lineInfoEntityDaoConfig = daoConfigMap.get(LineInfoEntityDao.class).clone();
        lineInfoEntityDaoConfig.initIdentityScope(type);

        blackListEntityDaoConfig = daoConfigMap.get(BlackListEntityDao.class).clone();
        blackListEntityDaoConfig.initIdentityScope(type);

        macKeyEntityDaoConfig = daoConfigMap.get(MacKeyEntityDao.class).clone();
        macKeyEntityDaoConfig.initIdentityScope(type);

        publicKeyEntityDaoConfig = daoConfigMap.get(PublicKeyEntityDao.class).clone();
        publicKeyEntityDaoConfig.initIdentityScope(type);

        scanInfoEntityDaoConfig = daoConfigMap.get(ScanInfoEntityDao.class).clone();
        scanInfoEntityDaoConfig.initIdentityScope(type);

        unionAidEntityDaoConfig = daoConfigMap.get(UnionAidEntityDao.class).clone();
        unionAidEntityDaoConfig.initIdentityScope(type);

        unionPayEntityDaoConfig = daoConfigMap.get(UnionPayEntityDao.class).clone();
        unionPayEntityDaoConfig.initIdentityScope(type);

        consumeCardDao = new ConsumeCardDao(consumeCardDaoConfig, this);
        blackListCardDao = new BlackListCardDao(blackListCardDaoConfig, this);
        cardRecordDao = new CardRecordDao(cardRecordDaoConfig, this);
        lineInfoEntityDao = new LineInfoEntityDao(lineInfoEntityDaoConfig, this);
        blackListEntityDao = new BlackListEntityDao(blackListEntityDaoConfig, this);
        macKeyEntityDao = new MacKeyEntityDao(macKeyEntityDaoConfig, this);
        publicKeyEntityDao = new PublicKeyEntityDao(publicKeyEntityDaoConfig, this);
        scanInfoEntityDao = new ScanInfoEntityDao(scanInfoEntityDaoConfig, this);
        unionAidEntityDao = new UnionAidEntityDao(unionAidEntityDaoConfig, this);
        unionPayEntityDao = new UnionPayEntityDao(unionPayEntityDaoConfig, this);

        registerDao(ConsumeCard.class, consumeCardDao);
        registerDao(BlackListCard.class, blackListCardDao);
        registerDao(CardRecord.class, cardRecordDao);
        registerDao(LineInfoEntity.class, lineInfoEntityDao);
        registerDao(BlackListEntity.class, blackListEntityDao);
        registerDao(MacKeyEntity.class, macKeyEntityDao);
        registerDao(PublicKeyEntity.class, publicKeyEntityDao);
        registerDao(ScanInfoEntity.class, scanInfoEntityDao);
        registerDao(UnionAidEntity.class, unionAidEntityDao);
        registerDao(UnionPayEntity.class, unionPayEntityDao);
    }
    
    public void clear() {
        consumeCardDaoConfig.clearIdentityScope();
        blackListCardDaoConfig.clearIdentityScope();
        cardRecordDaoConfig.clearIdentityScope();
        lineInfoEntityDaoConfig.clearIdentityScope();
        blackListEntityDaoConfig.clearIdentityScope();
        macKeyEntityDaoConfig.clearIdentityScope();
        publicKeyEntityDaoConfig.clearIdentityScope();
        scanInfoEntityDaoConfig.clearIdentityScope();
        unionAidEntityDaoConfig.clearIdentityScope();
        unionPayEntityDaoConfig.clearIdentityScope();
    }

    public ConsumeCardDao getConsumeCardDao() {
        return consumeCardDao;
    }

    public BlackListCardDao getBlackListCardDao() {
        return blackListCardDao;
    }

    public CardRecordDao getCardRecordDao() {
        return cardRecordDao;
    }

    public LineInfoEntityDao getLineInfoEntityDao() {
        return lineInfoEntityDao;
    }

    public BlackListEntityDao getBlackListEntityDao() {
        return blackListEntityDao;
    }

    public MacKeyEntityDao getMacKeyEntityDao() {
        return macKeyEntityDao;
    }

    public PublicKeyEntityDao getPublicKeyEntityDao() {
        return publicKeyEntityDao;
    }

    public ScanInfoEntityDao getScanInfoEntityDao() {
        return scanInfoEntityDao;
    }

    public UnionAidEntityDao getUnionAidEntityDao() {
        return unionAidEntityDao;
    }

    public UnionPayEntityDao getUnionPayEntityDao() {
        return unionPayEntityDao;
    }

}