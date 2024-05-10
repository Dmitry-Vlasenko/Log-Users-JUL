package com.dvlasenko.repository.impl;

import com.dvlasenko.entity.User;
import com.dvlasenko.repository.AppRepository;
import com.dvlasenko.utils.Constants;
import com.dvlasenko.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.MutationQuery;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserRepository implements AppRepository<User> {

    private final static String ENTITY_USERS = "User";
    private static final Logger LOGGER =
            Logger.getLogger(UserRepository.class.getName());
    @Override
    public String create(User user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            String hql = "INSERT INTO " + ENTITY_USERS + " (firstName, lastName, email) " +
                    "VALUES (:firstName, :lastName, :email)";
            MutationQuery query = session.createMutationQuery(hql);
            query.setParameter("firstName", user.getFirstName());
            query.setParameter("lastName", user.getLastName());
            query.setParameter("email", user.getEmail());
            query.executeUpdate();
            transaction.commit();
            return Constants.DATA_INSERT_MSG;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            LOGGER.log(Level.WARNING, Constants.LOG_DB_ERROR_MSG);
            return e.getMessage();
        }
    }

    public Optional<List<User>> read() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction;
            transaction = session.beginTransaction();
            List<User> list =
                    session.createQuery("FROM " + ENTITY_USERS, User.class)
                            .list();
            transaction.commit();
            return Optional.of(list);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, Constants.LOG_DB_ERROR_MSG);
            return Optional.empty();
        }
    }

    @Override
    public String update(User user) {
        if (readById(user.getId()).isEmpty()) {
            return Constants.DATA_ABSENT_MSG;
        } else {
            Transaction transaction = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();
                String hql = "UPDATE "+ ENTITY_USERS + " SET firstName = :firstName, lastName = :lastName, email = :email WHERE id = :id";
                MutationQuery query = session.createMutationQuery(hql);
                query.setParameter("firstName", user.getFirstName());
                query.setParameter("lastName", user.getLastName());
                query.setParameter("email", user.getEmail());
                query.setParameter("id", user.getId());
                query.executeUpdate();
                transaction.commit();
                return Constants.DATA_UPDATE_MSG;
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                LOGGER.log(Level.WARNING, Constants.LOG_DB_ERROR_MSG);
                return e.getMessage();
            }
        }
    }

    @Override
    public String delete(Long id) {
        if (readById(id).isEmpty()) {
            return Constants.DATA_ABSENT_MSG;
        } else {
            Transaction transaction = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();
                String hql = "DELETE FROM " + ENTITY_USERS + " WHERE id = :id";
                MutationQuery query = session.createMutationQuery(hql);
                query.setParameter("id", id);
                query.executeUpdate();
                transaction.commit();
                return Constants.DATA_DELETE_MSG;
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                LOGGER.log(Level.WARNING, Constants.LOG_DB_ERROR_MSG);
                return e.getMessage();
            }
        }
    }

    @Override
    public Optional<User> readById(Long id) {
        Optional<User> optional;
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            String hql = "FROM " + ENTITY_USERS + " WHERE id = :id";
            optional = Optional.ofNullable(session.createQuery(hql, User.class)
                    .setParameter("id", id)
                    .getSingleResult());
            transaction.commit();
            return optional;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            LOGGER.log(Level.WARNING, Constants.LOG_DB_ERROR_MSG);
            return Optional.empty();
        }
    }

}
