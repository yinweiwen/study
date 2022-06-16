from machine import Pin

class Button():

    def __init__(self, pin):
        self.buttonpin = Pin(pin, Pin.OUT)
        self.last_status = 1

    def set_state(self, state):
        self.buttonpin.value(state)
        self.last_status = state
        
    def state(self):
        return self.buttonpin.value()