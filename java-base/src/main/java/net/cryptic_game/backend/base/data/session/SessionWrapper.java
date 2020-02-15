package net.cryptic_game.backend.base.data.session;

import net.cryptic_game.backend.base.AppBootstrap;
import net.cryptic_game.backend.base.config.BaseConfig;
import net.cryptic_game.backend.base.config.Config;
import net.cryptic_game.backend.base.data.user.User;
import net.cryptic_game.backend.base.sql.SQLConnection;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

public class SessionWrapper {

    private static final SQLConnection sqlConnection;
    private static final Duration EXPIRE;

    static {
        final AppBootstrap app = AppBootstrap.getInstance();
        final Config config = app.getConfig();
        sqlConnection = app.getSqlConnection();
        EXPIRE = Duration.of(config.getAsInt(BaseConfig.SESSION_EXPIRE), ChronoUnit.DAYS);
    }

    public static Session openSession(final User user, final String deviceName) {
        final Session session = new Session();
        session.setUser(user);
        session.setToken(UUID.randomUUID());
        session.setDeviceName(deviceName);
        session.setExpire(LocalDateTime.now().plus(EXPIRE));
        session.setLastActive(LocalDateTime.now());
        session.setValid(true);

        final org.hibernate.Session sqlSession = sqlConnection.openSession();
        sqlSession.beginTransaction();
        sqlSession.save(session);
        sqlSession.getTransaction().commit();
        sqlSession.close();

        return session;
    }

    public static void closeSession(final Session session) {
        final org.hibernate.Session sqlSession = sqlConnection.openSession();
        sqlSession.beginTransaction();
        session.setValid(false);
        sqlSession.update(session);
        sqlSession.getTransaction().commit();
        sqlSession.close();
    }

    public static boolean isValid(final Session session) {
        return session.isValid() && LocalDateTime.now().isBefore(session.getExpire());
    }

    public static Session getSessionById(final UUID id) {
        final org.hibernate.Session sqlSession = sqlConnection.openSession();
        Session session = sqlSession.find(Session.class, id);
        sqlSession.close();

        return session;
    }

    public static Session getSessionByToken(final UUID token) {
        final org.hibernate.Session sqlSession = sqlConnection.openSession();
        final List<Session> sessions = sqlSession
                .createQuery("select object (s) from Session as s where s.token = :token", Session.class)
                .setParameter("token", token)
                .getResultList();
        sqlSession.close();

        if (!sessions.isEmpty()) return sessions.get(0);
        return null;
    }

    public static void setLastToCurrentTime(final Session session) {
        final org.hibernate.Session sqlSession = sqlConnection.openSession();
        sqlSession.beginTransaction();
        session.setLastActive(LocalDateTime.now());
        sqlSession.update(session);
        sqlSession.getTransaction().commit();
        sqlSession.close();
    }

    public static void deleteSessions(User user) {
        final org.hibernate.Session sqlSession = sqlConnection.openSession();
        sqlSession.beginTransaction();
        sqlSession.createQuery("delete from Session where user.id = :userId")
                .setParameter("userId", user.getId()).executeUpdate();
        sqlSession.getTransaction().commit();
        sqlSession.close();
    }
}
