package net.mortalsilence.indiepim.server.command.results;

import net.mortalsilence.indiepim.server.command.Result;
import net.mortalsilence.indiepim.server.dto.EmailAddressDTO;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: AmIEvil
 * Date: 13.11.12
 * Time: 23:15
 * To change this template use File | Settings | File Templates.
 */
public class EmailAddressListResult implements Result {

    private List<EmailAddressDTO> addresses;


   	public EmailAddressListResult() {
   	}

   	public EmailAddressListResult(List<EmailAddressDTO> addresses) {
   		this.addresses = addresses;
   	}

   	public List<EmailAddressDTO> getAddresses() {
   		return addresses;
   	}

}
