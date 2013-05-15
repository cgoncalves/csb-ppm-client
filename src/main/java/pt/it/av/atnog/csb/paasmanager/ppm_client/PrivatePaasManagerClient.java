package pt.it.av.atnog.csb.paasmanager.ppm_client;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;

import javax.ws.rs.core.Response.Status;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;

public class PrivatePaasManagerClient
{
	private String host;
	private String user;
	private String password;

	public PrivatePaasManagerClient(String host, String user, String password) {
		this.host = host;
		this.user = user;
		this.password = password;
	}

	public Status createPaas(String sshHost, String sshUser, String sshPassword, String id, String domain, String ip, String callback, String userEmail, String userPassword) throws Exception {

		callback = URLEncoder.encode(callback.toString(),"UTF-8");
		id = java.net.URLEncoder.encode(id, "UTF-8");
		userEmail = java.net.URLEncoder.encode(userEmail, "UTF-8");
		userPassword = java.net.URLEncoder.encode(userPassword, "UTF-8");

		String uri = new URI(
							String.format(
									"%s/setup?host=%s&user=%s&password=%s&id=%s&domain=%s&ip=%s&callback=%s&user_email=%s&user_password=%s",
									host, sshHost, sshUser, sshPassword, id, domain, ip, callback, userEmail, userPassword)
									)
						.toASCIIString();
		URL url = new URI(uri).toURL();

        DefaultHttpClient client = new DefaultHttpClient();
        client.getCredentialsProvider().setCredentials(new AuthScope(url.getHost(), url.getPort()), new UsernamePasswordCredentials(user, password));

        // Create AuthCache instance
        // Add AuthCache to the execution context
        AuthCache authCache = new BasicAuthCache();
        BasicScheme basicAuth = new BasicScheme();
        authCache.put(new HttpHost(url.getHost(), url.getPort()), basicAuth);
        BasicHttpContext localcontext = new BasicHttpContext();
        localcontext.setAttribute(ClientContext.AUTH_CACHE, authCache);

        HttpPost post = new HttpPost(uri);
        HttpResponse response = client.execute(post, localcontext);
        return Status.fromStatusCode(response.getStatusLine().getStatusCode());
	}

	public static void main(String[] args) throws URISyntaxException, MalformedURLException, UnsupportedEncodingException {
		String host = "http://localhost:8080";
		String id = java.net.URLEncoder.encode("PTIn OpenStack", "UTF-8");
		System.out.println(String.format("%s/setup?id=%s", host, id));
		String uri = new URI(String.format("%s/setup?id=%s", host, id)).toString();
		URL url = new URI(uri).toURL();
		System.out.println(url.toString());

	}
}
