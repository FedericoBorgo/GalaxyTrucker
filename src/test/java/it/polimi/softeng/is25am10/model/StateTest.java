package it.polimi.softeng.is25am10.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;

class StateTest {
    private State state;
    private Model testModel;

    @BeforeEach
    void setUp() {
        testModel = new Model(2, (model, type) -> {});
        state = new State(State.Type.JOINING, (m, t) -> {}, testModel);
    }

    @Test
    void testEquals() throws InterruptedException {
        assert (state.equals(state));
        assert (!state.equals(null));
        assert (!state.equals("test"));
        State other = new State(State.Type.BUILDING, (m, t) -> {}, testModel);
        assert (!state.equals(other));
        State other2 = new State(State.Type.JOINING, (m, t) -> {}, testModel);
        assert (state.equals(other2));

        state.next(State.Type.BUILDING);
        other2.next(State.Type.BUILDING);
        assert (state.equals(other2));
    }

    @Test
    void setNotifyTest() {
        AtomicBoolean called = new AtomicBoolean(false);
        BiConsumer<Model, State.Type> notify = (m, t) -> called.set(true);
        state.setNotify(notify);
        state.notify.accept(testModel, State.Type.BUILDING);
        assertTrue(called.get());
    }

    @Test
    void testMethods() throws InterruptedException {
        assertEquals(State.Type.JOINING, state.get());
        assertEquals("Aspettare giocatori",state.get().getName());
        state.next(State.Type.BUILDING);
        assertEquals(State.Type.JOINING, state.getPrev());
        assertEquals(State.Type.BUILDING, state.get());
        assertEquals("Assemblare",state.get().getName());
        state.next(State.Type.CHECKING);
        assertEquals("Controllare connettori",state.get().getName());
        state.next(State.Type.ALIEN_INPUT);
        assertEquals("Piazzare equipaggio",state.get().getName());
        state.next(State.Type.DRAW_CARD);
        assertEquals("Pescare carta",state.get().getName());
        state.next(State.Type.WAITING_INPUT);
        assertEquals("Dichiarare scelte",state.get().getName());
        state.next(State.Type.PLACE_REWARD);
        assertEquals("Piazzare scatole",state.get().getName());
        state.next(State.Type.ENDED);
        assertEquals("Terminata",state.get().getName());
        state.next(State.Type.PAUSED);
        assertEquals("In pausa",state.get().getName());
        state.next(State.Type.PAY_DEBT);
        assertEquals("Gettare elementi",state.get().getName());
    }
}