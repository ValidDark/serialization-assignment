package com.cooksys.serialization.assignment.model;

public class Student {
    private Contact contact;

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

	@Override
	public String toString()
	{
		return contact.getFirstName() + " " + contact.getLastName() + "  " + contact.getEmail() + "  " + contact.getPhoneNumber();
	}
}
