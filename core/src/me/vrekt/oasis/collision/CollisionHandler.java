package me.vrekt.oasis.collision;

import com.badlogic.gdx.physics.box2d.*;
import me.vrekt.oasis.entity.player.EntityPlayer;

/**
 * A basic collision handler for any world.
 */
public final class CollisionHandler implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        final Fixture fixtureA = contact.getFixtureA();
        final Fixture fixtureB = contact.getFixtureB();

        // process player collision
        // ensure we are not colliding with another player
        if (fixtureA.getUserData() instanceof EntityPlayer && !(fixtureB.getUserData() instanceof EntityPlayer))
            ((EntityPlayer) fixtureA.getUserData()).startCollision(fixtureB);
    }

    @Override
    public void endContact(Contact contact) {
        final Fixture fixtureA = contact.getFixtureA();

        // end player collision
        if (fixtureA.getUserData() instanceof EntityPlayer)
            ((EntityPlayer) fixtureA.getUserData()).endCollision();
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        final Fixture fixtureA = contact.getFixtureA();
        final Fixture fixtureB = contact.getFixtureB();

        // disable collisions between players
        if (fixtureA.getUserData() instanceof EntityPlayer
                && fixtureB.getUserData() instanceof EntityPlayer) contact.setEnabled(false);
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
