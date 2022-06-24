export type Payload = Record<string, string | boolean | undefined>;

type Listener = (event: Payload) => void;

let nextId = 0;
const listeners = new Map<number, Listener>();

/**
 * Dispatch an event to the listener associated with the id.
 */
export function dispatchEvent(event: Payload, id: number) {
  const listener = listeners.get(id);
  if (listener != null) {
    listener(event);
  }
}

/**
 * Register an event listener. Use returned id to dispatch
 * events to this listener.
 */
export function subscribeEvents(listener: Listener) {
  const id = nextId;
  nextId += 1;
  listeners.set(id, listener);
  return {
    id,
    unsubscribe: () => {
      listeners.delete(id);
    },
  };
}
