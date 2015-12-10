package in.appsplanet.wedsource.utils;

public interface Constants {
    // ************************************URL*****************************************
    // BASE URL
    // String URL_BASE = "http://weddsource.com/ReceiveUploads.php?";
    String URL_BASE = "http://weddsource.com/webservice/ReceiveUploads.php?";


    String URL_PRIVACY_POLICY = "http://weddsource.com/webservice/privacy.html";
    // ************************************COMMAND*****************************************

    String PARAM_COMMAND = "Command";
    String PARAM_USERNAME = "username";
    String PARAM_PASSWORD = "password";
    String PARAM_COUNTRY = "Country";
    String PARAM_USERID = "userId";
    String PARAM_NOTESID = "notesId";
    String PARAM_EVENTID = "eventId";
    String PARAM_PLAYLISTID = "playlistId";
    String PARAM_NAME = "name";
    String PARAM_VENUE = "venue";
    String PARAM_DATE = "date";
    String PARAM_TIME = "time";
    String PARAM_DESCRIPTION = "description";
    String PARAM_ADDRESS = "address";
    String PARAM_EMAIL = "email";
    String PARAM_PHONE = "phone";
    String PARAM_WEBSITE = "website";
    String PARAM_AMOUNT = "amount";
    String PARAM_TYPE = "type";
    String PARAM_BUDGET = "budget";
    String PARAM_COUNTRYID = "countryId";
    String PARAM_CATEGORYID = "categoryId";
    String PARAM_VENDORID = "vendorId";
    String PARAM_SONGSID = "songsId";
    String PARAM_NOOFPERSONS = "noOfPersons";
    String PARAM_FINANCEID = "financeId";
    String PARAM_TRANSACTIONID= "transactionId";
    String PARAM_GUESTID= "guestId";


    String COMMAND_USERLOGIN = "UserLogin";
    String COMMAND_FORGOTPASSWORD = "ForgotPassword";
    String COMMAND_GETCOUNTRY = "GetCountry";
    String COMMAND_GETCATEGORY = "GetCategory";
    String COMMAND_GETVENDOR = "GetVendor";
    String COMMAND_ADDVENDOR = "AddVendor";
    String COMMAND_EDITVENDOR= "EditVendor";
    String COMMAND_DELETEMYVENDOR= "DeleteMyVendor";
    String COMMAND_GETEVENT = "GetEvent";
    String COMMAND_DELETEEVENT = "DeleteEvent";
    String COMMAND_GETEVENT_GUEST = "GetEventsGuest";
    String COMMAND_ADDEVENT = "AddEvent";
    String COMMAND_EDITEVENT = "EditEvent";
    String COMMAND_GETGUEST = "GetGuest";
    String COMMAND_DELETEGUEST = "DeleteGuest";
    String COMMAND_GETPROMOTION = "GetPromotion";
    String COMMAND_MAKEVENDOR = "MakeVendor";
    String COMMAND_ADDGUEST = "AddGuest";
    String COMMAND_GETFINANCE = "GetFinance";
    String COMMAND_ADDFINANCE = "AddFinance";
    String COMMAND_GETTRANSACTION = "GetTransaction";
    String COMMAND_ADDTRANSACTION = "AddTransaction";
    String COMMAND_DELETETRANSACTION = "DeleteTransaction";
    String COMMAND_GETNOTES = "GetNotes";
    String COMMAND_ADDNOTES = "AddNotes";
    String COMMAND_EDITNOTES = "EditNotes";

    String COMMAND_DELETENOTES = "DeleteNotes";
    String COMMAND_GETPLAYLIST = "GetPlaylist";
    String COMMAND_ADDPLAYLIST = "AddPlaylist";
    String COMMAND_DELETEPLAYLIST = "DeletePlaylist";
    String COMMAND_GETSONGS = "GetSongs";
    String COMMAND_DELETESONGS = "DeleteSongs";

    String COMMAND_ADDSONGS = "AddSongs";

    // ************************************INTENT*****************************************
    String INTENT_COUNTRY = "Country";
    String INTENT_CATEGORY = "Category";
    String INTENT_VENDOR = "Vendor";
    String INTENT_NOTES = "Notes";
    String INTENT_EVENT = "Event";
    String INTENT_EVENT_DATE = "Event_Date";
    String INTENT_GUEST = "Guest";
    String INTENT_FINANCE = "Finance";
    String INTENT_PLAYLIST = "Playlist";
    String INTENT_IS_MY_VENDOR = "is_my_vendor";

    String DATE_FORMAT = "MMMM dd yyyy";

}
