import { dispatchEvent, subscribeEvents } from '../event';

it('dispatches events', () => {
  const listener = jest.fn();
  const { id, unsubscribe } = subscribeEvents(listener);

  dispatchEvent({ key: 'value1' }, id + 1);
  expect(listener).not.toHaveBeenCalled();

  dispatchEvent({ key: 'value2' }, id);
  expect(listener).toHaveBeenCalledTimes(1);
  expect(listener).toHaveBeenCalledWith({ key: 'value2' });

  listener.mockClear();
  unsubscribe();
  dispatchEvent({ key: 'value3' }, id);
  expect(listener).not.toHaveBeenCalled();
});
