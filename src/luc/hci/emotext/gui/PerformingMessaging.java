package luc.hci.emotext.gui;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import luc.hci.emotext.R;
import luc.hci.emotext.info_type.InfoOfFriend;
import luc.hci.emotext.info_type.InfoOfMessage;
import luc.hci.emotext.interfaces.Manager;
import luc.hci.emotext.service.MessagingService;
import luc.hci.emotext.toolbox.ControllerOfFriend;
import luc.hci.emotext.toolbox.StorageManipulater;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PerformingMessaging extends Activity
{

	private static final int MESSAGE_CANNOT_BE_SENT = 0;
	public String username;
	private EditText messageText;
	private ListView messageHistoryText;
	private TextView timeStamp;
	private ImageButton emotionImage;
	private ImageButton sendMessageButton;
	private ImageButton camButton;
	private Manager imService;
	private InfoOfFriend friend = new InfoOfFriend( );
	private StorageManipulater localstoragehandler;
	private Cursor dbCursor;
	private AwesomeAdapter adapter;
	private List< Message > messages;

	private ServiceConnection mConnection = new ServiceConnection( )
	{

		public void onServiceConnected( ComponentName className, IBinder service )
		{
			imService = ( ( MessagingService.IMBinder ) service ).getService( );
		}

		public void onServiceDisconnected( ComponentName className )
		{
			imService = null;
			Toast.makeText( PerformingMessaging.this, R.string.local_service_stopped, Toast.LENGTH_SHORT ).show( );
		}
	};

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		// TODO Auto-generated method stub
		super.onCreate( savedInstanceState );

		setContentView( R.layout.message ); // messaging_screen);

		messageHistoryText = ( ListView ) findViewById( R.id.messageHistory );
		messageHistoryText.setDivider( null );
		messageHistoryText.setDividerHeight( 0 );
		messages = new ArrayList< Message >( );

		// Camera button to send pictures / interpret emotion
		//camButton = ( ImageButton ) findViewById( R.id.camButton );
		
		emotionImage = ( ImageButton ) findViewById( R.id.emotionImage );
		messageText = ( EditText ) findViewById( R.id.message );
		messageText.requestFocus( );
		sendMessageButton = ( ImageButton ) findViewById( R.id.sendMessageButton );
		Bundle extras = this.getIntent( ).getExtras( );

		// ActionBar
		ActionBar actionBar = getActionBar( );
		getActionBar( ).setDisplayHomeAsUpEnabled( true );

		friend.userName = extras.getString( InfoOfFriend.USERNAME );
		friend.ip = extras.getString( InfoOfFriend.IP );
		friend.port = extras.getString( InfoOfFriend.PORT );
		String msg = extras.getString( InfoOfMessage.MESSAGETEXT );

		setTitle( "Messaging with " + friend.userName );

		localstoragehandler = new StorageManipulater( this );
		dbCursor = localstoragehandler.get( friend.userName, MessagingService.USERNAME );

		if ( dbCursor.getCount( ) > 0 )
		{
			int noOfScorer = 0;
			dbCursor.moveToFirst( );
			while ( ( !dbCursor.isAfterLast( ) ) && noOfScorer < dbCursor.getCount( ) )
			{
				noOfScorer++;

				this.appendToMessageHistory( dbCursor.getString( 2 ), dbCursor.getString( 3 ) );
				dbCursor.moveToNext( );
			}
		}
		localstoragehandler.close( );

		if ( msg != null )
		{
			this.appendToMessageHistory( friend.userName, msg );
			( ( NotificationManager ) getSystemService( NOTIFICATION_SERVICE ) ).cancel( ( friend.userName + msg ).hashCode( ) );
		}

		adapter = new AwesomeAdapter( getApplicationContext( ), messages );
		messageHistoryText.setAdapter( adapter );

		emotionImage.setOnClickListener( new View.OnClickListener( )
		{
			public void onClick( View v )
			{

				Toast.makeText( getApplicationContext( ), R.string.app_name, Toast.LENGTH_SHORT ).show( );
				// Intent to take a picture and send it on the message.

			}
		} );

		sendMessageButton.setOnClickListener( new OnClickListener( )
		{
			CharSequence message;
			Handler handler = new Handler( );

			public void onClick( View arg0 )
			{
				message = messageText.getText( );
				if ( message.length( ) > 0 )
				{
					addNewMessage( new Message( message.toString( ), true ) );

					localstoragehandler.insert( imService.getUsername( ), friend.userName, message.toString( ) );

					messageText.setText( "" );
					Thread thread = new Thread( )
					{
						public void run( )
						{
							try
							{
								if ( imService.sendMessage( imService.getUsername( ), friend.userName, message.toString( ) ) == null )
								{

									handler.post( new Runnable( )
									{

										public void run( )
										{

											Toast.makeText( getApplicationContext( ), R.string.message_cannot_be_sent, Toast.LENGTH_LONG ).show( );

											// showDialog(MESSAGE_CANNOT_BE_SENT);
										}

									} );
								}
							}
							catch ( UnsupportedEncodingException e )
							{
								Toast.makeText( getApplicationContext( ), R.string.message_cannot_be_sent, Toast.LENGTH_LONG ).show( );

								e.printStackTrace( );
							}
						}
					};
					thread.start( );
				}

			}
		} );

		messageText.setOnKeyListener( new OnKeyListener( )
		{
			public boolean onKey( View v, int keyCode, KeyEvent event )
			{
				if ( keyCode == 66 )
				{
					sendMessageButton.performClick( );
					return true;
				}
				return false;
			}

		} );

		messageHistoryText.setSelection( messages.size( ) - 1 );
	}

	@Override
	protected Dialog onCreateDialog( int id )
	{
		int message = -1;
		switch ( id )
		{
		case MESSAGE_CANNOT_BE_SENT:
			message = R.string.message_cannot_be_sent;
			break;
		}

		if ( message == -1 )
		{
			return null;
		}
		else
		{
			return new AlertDialog.Builder( PerformingMessaging.this ).setMessage( message ).setPositiveButton( R.string.OK, new DialogInterface.OnClickListener( )
			{
				public void onClick( DialogInterface dialog, int whichButton )
				{
					/* User clicked OK so do some stuff */
				}
			} ).create( );
		}
	}

	@Override
	protected void onPause( )
	{
		super.onPause( );
		unregisterReceiver( messageReceiver );
		unbindService( mConnection );

		ControllerOfFriend.setActiveFriend( null );

	}

	@Override
	protected void onResume( )
	{
		super.onResume( );
		bindService( new Intent( PerformingMessaging.this, MessagingService.class ), mConnection, Context.BIND_AUTO_CREATE );

		IntentFilter i = new IntentFilter( );
		i.addAction( MessagingService.TAKE_MESSAGE );

		registerReceiver( messageReceiver, i );

		ControllerOfFriend.setActiveFriend( friend.userName );

	}

	public class MessageReceiver extends BroadcastReceiver
	{

		@Override
		public void onReceive( Context context, Intent intent )
		{
			Bundle extra = intent.getExtras( );
			String username = extra.getString( InfoOfMessage.USERID );
			String message = extra.getString( InfoOfMessage.MESSAGETEXT );

			if ( username != null && message != null )
			{
				if ( friend.userName.equals( username ) )
				{
					addNewMessage( new Message( message, false ) );
					localstoragehandler.insert( username, imService.getUsername( ), message );

				}
				else
				{
					if ( message.length( ) > 15 )
					{
						message = message.substring( 0, 15 );
					}
					Toast.makeText( PerformingMessaging.this, username + " says '" + message + "'", Toast.LENGTH_SHORT ).show( );
				}
			}
		}

	};

	private MessageReceiver messageReceiver = new MessageReceiver( );

	public void appendToMessageHistory( String username, String message )
	{
		Log.d( "append", username + message );
		if ( username != null && message != null )
		{
			if ( username.equals( friend.userName ) )
			{
				messages.add( new Message( message, false ) );
				setBackgroundEmotionColor( message );
			}
			else
			{
				messages.add( new Message( message, true ) );
			}
		}
	}

	private void addNewMessage( Message m )
	{
		if ( !m.isMine( ) )
		{
			setBackgroundEmotionColor( m.getMessage( ) );
		}
		messages.add( m );
		adapter.notifyDataSetChanged( );
		messageHistoryText.setSelection( messages.size( ) - 1 );
	}

	private void setBackgroundEmotionColor( String message )
	{
		// HAPPY, GREEN
		if ( EmotionEvaluation.happyMessage( message ) )
		{
			setEmotionImageAndBackground( R.drawable.happiness, Color.GREEN );
		}
		// SURPRISE, YELLOW
		else if ( EmotionEvaluation.surpriseMessage( message ) )
		{
			setEmotionImageAndBackground( R.drawable.surprise, Color.YELLOW );
		}
		// ANGRY, RED
		else if ( EmotionEvaluation.angryMessage( message ) )
		{
			setEmotionImageAndBackground( R.drawable.anger, Color.RED );
		}
		// DISGUST, MAGENTA
		else if ( EmotionEvaluation.disgustMessage( message ) )
		{
			setEmotionImageAndBackground( R.drawable.disgust, Color.rgb( 115, 29, 146 ) );
		}
		// FEAR, ORANGE
		else if ( EmotionEvaluation.fearMessage( message ) )
		{
			setEmotionImageAndBackground( R.drawable.fear, Color.rgb( 255, 153, 0 ) );
		}
		// SAD, BLUE
		else if ( EmotionEvaluation.sadMessage( message ) )
		{
			setEmotionImageAndBackground( R.drawable.sadness, Color.BLUE );
		}
		// NEUTRAL, DEFAULT
		else
		{
			setEmotionImageAndBackground( 0, Color.TRANSPARENT );
		}
	}

	private void setEmotionImageAndBackground( int image, int color )
	{
		emotionImage.setImageResource( image );
		emotionImage.setBackgroundColor( color );
	}

	@Override
	protected void onDestroy( )
	{
		super.onDestroy( );
		if ( localstoragehandler != null )
		{
			localstoragehandler.close( );
		}
		if ( dbCursor != null )
		{
			dbCursor.close( );
		}
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item )
	{
		switch ( item.getItemId( ) )
		{
		case android.R.id.home:
			// app icon in action bar clicked; go home
			Intent actionBar = new Intent( this, ListOfFriends.class );
			actionBar.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
			startActivity( actionBar );
			return true;
		default:
			return super.onOptionsItemSelected( item );
		}
	}
}