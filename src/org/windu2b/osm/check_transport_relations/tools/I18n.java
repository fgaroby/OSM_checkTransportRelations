// License: GPL. Copyright 2007 by Immanuel Scholz and others
package org.windu2b.osm.check_transport_relations.tools;

import java.text.MessageFormat;
import java.util.HashMap;

/**
 * Internationalisation support.
 * 
 * @author Immanuel.Scholz
 */
public class I18n
{
	private static HashMap<String, String>	 strings	 = null;


	private static HashMap<String, String[]>	pstrings	= null;




	/**
	 * Translates some text for the current locale. These strings are collected
	 * by a script that runs on the source code files. After translation, the
	 * localizations are distributed with the main program. <br/>
	 * For example, {@code tr("JOSM''s default value is '' 0}''.", val)}. <br/>
	 * Use {@link #trn} for distinguishing singular from plural text, i.e., do
	 * not use {@code tr(size == 1 ? "singular" : "plural")} nor
	 * {@code size == 1 ? tr("singular") : tr("plural")}
	 * 
	 * @param text
	 *            the text to translate. Must be a string literal. (No constants
	 *            or local vars.) Can be broken over multiple lines. An
	 *            apostrophe ' must be quoted by another apostrophe.
	 * @param objects
	 *            the parameters for the string. Mark occurrences in
	 *            {@code text} with {@code 0} , {@code 1} , ...
	 * @return the translated string.
	 * @see #trn
	 * @see #trc
	 * @see #trnc
	 */
	public static final String tr( String text, Object... objects )
	{
		if ( text == null ) return null;
		return MessageFormat.format( gettext( text, null ), objects );
	}




	/**
	 * Translates some text in a context for the current locale. There can be
	 * different translations for the same text within different contexts.
	 * 
	 * @param context
	 *            string that helps translators to find an appropriate
	 *            translation for {@code text}.
	 * @param text
	 *            the text to translate.
	 * @return the translated string.
	 * @see #tr
	 * @see #trn
	 * @see #trnc
	 */
	public static final String trc( String context, String text )
	{
		if ( context == null ) return tr( text );
		if ( text == null ) return null;
		return MessageFormat.format( gettext( text, context ), ( Object ) null );
	}




	/**
	 * Translates some text for the current locale and distinguishes between
	 * {@code singularText} and {@code pluralText} depending on {@code n}. <br/>
	 * For instance, {@code trn("There was an error!", "There were errors!", i)}
	 * or {@code trn("Found 0} error in {1}!", "Found {0} errors in {1}!", i,
	 * Integer.toString(i), url)}.
	 * 
	 * @param singularText
	 *            the singular text to translate. Must be a string literal. (No
	 *            constants or local vars.) Can be broken over multiple lines.
	 *            An apostrophe ' must be quoted by another apostrophe.
	 * @param pluralText
	 *            the plural text to translate. Must be a string literal. (No
	 *            constants or local vars.) Can be broken over multiple lines.
	 *            An apostrophe ' must be quoted by another apostrophe.
	 * @param n
	 *            a number to determine whether {@code singularText} or
	 *            {@code pluralText} is used.
	 * @param objects
	 *            the parameters for the string. Mark occurrences in
	 *            {@code singularText} and {@code pluralText} with {@code 0} ,
	 *            {@code 1} , ...
	 * @return the translated string.
	 * @see #tr
	 * @see #trc
	 * @see #trnc
	 */
	public static final String trn( String singularText, String pluralText,
	        long n, Object... objects )
	{
		return MessageFormat.format(
		        gettextn( singularText, pluralText, null, n ), objects );
	}




	private static final String gettext( String text, String ctx, boolean lazy )
	{
		int i;
		if ( ctx == null && text.startsWith( "_:" )
		        && ( i = text.indexOf( "\n" ) ) >= 0 )
		{
			ctx = text.substring( 2, i - 1 );
			text = text.substring( i + 1 );
		}
		if ( strings != null )
		{
			String trans = strings.get( ctx == null ? text : "_:" + ctx + "\n"
			        + text );
			if ( trans != null ) return trans;
		}
		if ( pstrings != null )
		{
			String[] trans = pstrings.get( ctx == null ? text : "_:" + ctx
			        + "\n" + text );
			if ( trans != null ) return trans[0];
		}
		return lazy ? gettext( text, null ) : text;
	}




	private static final String gettextn( String text, String plural,
	        String ctx, long num )
	{
		int i;
		if ( ctx == null && text.startsWith( "_:" )
		        && ( i = text.indexOf( "\n" ) ) >= 0 )
		{
			ctx = text.substring( 2, i - 1 );
			text = text.substring( i + 1 );
		}
		if ( pstrings != null )
		{
			i = pluralEval( num );
			String[] trans = pstrings.get( ctx == null ? text : "_:" + ctx
			        + "\n" + text );
			if ( trans != null && trans.length > i ) return trans[i];
		}

		return num == 1 ? text : plural;
	}




	private static int pluralEval( long n )
	{
		return ( ( n != 1 ) ? 1 : 0 );
	}




	private static final String gettext( String text, String ctx )
	{
		return gettext( text, ctx, false );
	}
}
