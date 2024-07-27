package com.nmims.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nmims.beans.BodBean;
import com.nmims.beans.MettlFetchTestResultBean;
import com.nmims.beans.MettlResultsSyncBean;
import com.nmims.beans.MettlStudentTestInfo;
import com.nmims.beans.ResponseListBean;

@Service
public class PGApplyBODService {
	
	//Question Id's for June 2022
	//List<String> questionIds = Arrays.asList("wzEnGnG6zenhJ66UI81j%2FBc7fXxb5jlQkdr1p0JePJA%3D","lf6F8JeYqESCmoUE%2FmhRReyRT9CJHX280pU0lDh5y4I%3D","8XBql01MbomkS3cdFDKZIpeoLlFblex6vjR5pP%2FIuoo%3D","oyMytT2Z%2BzTFVYaVl54rYLNliQguAPXw%2BIgkbEicTZA%3D","ZNJ9a%2FLhFoDWLktRXgJGthhtYGf55FVX7K3WnZZjJYA%3D","GSpU9nBwfB66vlKACZDxK%2BNIdkXQUEcYN%2FsCHL%2F8YFI%3D","QSp7wAZW%2FhPdZtY2GN2hFpgIxI8SnyuWIrwSEpzqeVk%3D","GFHgiHq6Pj1vF7Wft5bZgCXE1ysZMx7sBge4mf4TOKk%3D","bYf4l5fn25Co4oL5cC1Fe5mEmi9FqrYelnmxlyizkjo%3D","W2lvj%2FHZ3OaLzlw2qhhrYJsko%2FmWcQ%2F%2BnSH%2BUPqWf8g%3D","RnrBPXqtOn5OUh4nQi28kNnzEgzh3vRxcsgFufrWKOM%3D","NPAxJIOYlUn6ev7PSSpmlD%2BoY2grx80%2F2K63oEUPSLk%3D","txul5ZYcb4zjBfz9GiERwBt0fmUnDHk6mBZ4o5xgNO8%3D","%2FXvBshUVi5ulIorTh5NxteXDGHKWCzUQZK0F0anvXQc%3D","A%2BorVwOVCNudkUFyJCp2scQZ0glQB2gtxxWgMd%2BTDUY%3D","24l5B8Ksi8wSG3xjgMVG1yXE1ysZMx7sBge4mf4TOKk%3D","ViiNLWZKmdOWU44l8zw8xS8PqqwC0U9%2B3w25oCKYsBA%3D","80inBL%2Fx8TZa%2FJXnBAkdPnSq0McETKGIdv6Bh8%2FHagw%3D","OhEjNJ%2FOURSuuSMdCnVbZyIzb6upq7NWruYi%2Bt2tlgM%3D","wDkFkG7C2w%2B4YftClerJGBcWHZ1X0vAA%2BTsHFg20yyQ%3D","rUO6RFIxob5KhQCRQxEC2X2RF%2F0m18uTbSjJDUgpGOo%3D","tvLzKj8khVW8Jp%2FuwvosOKLn7h92NVSCJuiv%2BUZZ%2BWQ%3D","fHWqNqRPvEcf9aAe8lhhVmjQF%2FT%2BL8vqKlKGORT8Pys%3D","1DiafMIpidU35oxAKaFCNXSq0McETKGIdv6Bh8%2FHagw%3D","%2FE2UIN5GX2T4by%2FSgKDAKjQNIomkyk8N%2Bd5JdMG7ppo%3D","flMk6nj7EHsqO5h6A0C3pQ3EYFzBiKRVYIiuGW73618%3D","LzO4mVglHtcyOHeey3z0l%2FfKKFk2uN3d8RAELgSs%2Bbw%3D","Z5AVnuB1kKPEJgcD6Omss%2BhfOg%2FOFodSz1ydy3Aj2bw%3D","4vSTQcLA%2BFNBYkEGd%2BZ%2B0%2BqPPLM7w3PDigUxH%2BL2LU0%3D","ywshOdCbPTdtGJmuh8S65s2hqy5E1GOobFITQartQoA%3D","h3wnJgXngviNiFC8KRCVHprP7AvaZdx5r4CROJ1xfoc%3D","6CkNS9ujtGKP0HKk%2Fkrg1mfe%2BcnGTq1YbZuYwnsOHoY%3D","YvESfyBpC7lkL8GZZ%2F6l7Wpkcu0%2BgvvhopW2kC1Qcas%3D","1ERf%2Fi0%2BWTHHfSIKAMZYJmEnoxYn28s371UBYCstwoM%3D","ZvmZvzUKK%2B1X9B0zDCiv8zrQSnUp6QT1ukU6db9J2HI%3D","bYo6Z3qUg0dEPtPRQhL9jow%2BlDGzXRIRo4wE951F5E8%3D","2%2B3b%2FR2JdwLQI5DrmV1G9MUNra5eeXI6VStkfBJhppE%3D","2UJD%2FuLBUCpW08Q5HxrmiLctTpRt4UMHI67QC8vb%2Ftc%3D","8y8J2yXHQnlNMegvh2AFEIJ%2BgSVXRaZT4O%2FnDEJJVXQ%3D","fc83rZumI0OzgbIw49aZ%2BMibuREAMzGJUbJjv61%2BEVg%3D","zQfegK8D1ITxVcAYaMrPbA3sI6APPeLYOiI%2FywWgcXs%3D","ClyyWMQWPG7brKDGZmSaXFtkwkUIPxu8Lg8xgzAhtU8%3D","br379VgP01nbqpA9n%2F4Vmc2Ibr93h%2FfnTcEmBFtk5po%3D","8QHaeUGcD%2FIT09CavVkaclSoy%2Fzq1PHKs2gDB5lZUoQ%3D","J5xCdTckDrpQl0Du4auAFgO9MabewwFGaFFN1vWXSxo%3D","QjVfc49XK0P5XYBUrzYCA%2FXSyyzw5RzRlauQrrFzRrY%3D","1efBW8LHFpKhhXvy61JMSw%2BXtn%2FtHktQovz81vCaKxY%3D","dkRCbcHaFPFhHlT8fL2PVIyAp2PN0D0uYEd5zdBP%2Fuo%3D","BH%2FHLXUJTR4n%2B%2B7yDFAKaLU0JWO9D5fD0105gfn14bc%3D","2qrDqX3RSqfI5HUg%2BGo2sJNolCSSQXiTiPRmccwpDOI%3D","niJhG9LyCjTv44uVuZrGUezGyrUo35yZxBJyKw2QQW8%3D","w947TcPQjkSRstY0cFvALsZm9ML3qnsqbKFz%2BpOfFmQ%3D","kdu0HRGMwTAwTZN8tTa%2FbcQZ0glQB2gtxxWgMd%2BTDUY%3D","Ta8V%2FXxVCxlDOWxlZF5Il7BsK8XNymSbuIhmZSoFeJQ%3D","%2BkdNETO7%2FPw2P00pQs3X0CTUOAm6Lmy8fldlkom0yGE%3D","Zx6c2HXYLDMJ%2BAR99NYaZXweFs1WPxEsGF0U0JuO2ok%3D","b0QEoyKsPDNVYOBmAr4f4Ca3RuSSqg%2ByCU6Iwrzx9GM%3D","6uKw0oLhwHcODPdiPAuJwsQZ0glQB2gtxxWgMd%2BTDUY%3D","UqNkyU1%2FDYCGDiKRYfaCrsrR1MsS1LbJIncq65vE83E%3D","OUM4mogcMKGeMbx9T6r2amEnoxYn28s371UBYCstwoM%3D","e%2FrYh2pE%2FKyFV7hu3pbKC6%2FnT6GI4jJPEFUsHEO78Q0%3D","dY4TLOt1W0Lq%2B0l01RTontnVFsaK3asTUbBl1tiMOa8%3D","RfdEji9UM0R7t9X7f%2FiAndkDHTGa%2B1aNVIF1M4YBX7o%3D","eMTUuEAPDWSaMUK0xhMn4vYGMeHSQuvglrbyx3QVO2Y%3D","5rgBgypoUUjtwsQvr2VMbSiOp1dichNBsAriFiMFhzw%3D","FudROKQp2zNPuhJqgS1IdZiI1YFhQaC8dlPFraFSQXM%3D","rlfOELkIs%2Fm0otqXGL2wzxV37vIyDwjDsmL8IhLkyqo%3D","Cco31Ag2TYt0FIXkLHzmJ4n8fyr84RHDrFvBxhJnGZE%3D","x%2BGmT2OQXRE%2BtPgeErUbaPLYFIuc24uMuE1WuCNXsGk%3D","1NhtzNx8DR3HvkE%2FIiIjcqT1rUEch0nQoDUakS2VyUM%3D","nD9IpwrTyxX7Eq91H1BhOequHdIk6yH3vTFPNjliuL8%3D","T0DUqUQGUR1ZTcu6btBF7odf03KBd6R4PE%2BjDyGO2wQ%3D","6fVTjZA0jC4V0PDsUc4z4QfpfSKpXU2Cnzl1M9rN6Ig%3D","DPxReFZrAefvmsRsbMVE0PH4jq2RRG8B96yuUMpFmJ8%3D","0HNEk4MAEEuVQ3UUPMeL1B1LiSccQ4JQ0JqaO4NgheQ%3D","n96aczyfL4FrG0Qg%2BLbd1uXDGHKWCzUQZK0F0anvXQc%3D","VvfFNnbqcIhwf2FPcx0GphYxncA9PHdg4m%2Bc7iTrzck%3D","1NFX16iZO8z7JUGo31DK4NnN%2BjJpZ4tFtk8D81kkK%2Fs%3D","EMHiwQ0HZEX3GbYQgnyfSFqbVaaycUzxiaRAwBc%2BrnM%3D","gRYIPeCmOHGTjfSPDiwcy%2BZrqpggOjKCepmQKDXrrX0%3D","K2iLZP1pprb0BITUoRC%2By8UNra5eeXI6VStkfBJhppE%3D","%2BuhNAa2wS8kSbtZiOvWfgzocIg1qHSzcMXVkHhnFGU8%3D","8LYLbPupmL%2F0QpIYpGngOuzGyrUo35yZxBJyKw2QQW8%3D","rLwtlV9NtWycAivO6z1pojocIg1qHSzcMXVkHhnFGU8%3D","Zih2BYpkHLaWPcleXfdHueNCyXIWwMUiHh9LcnTWs%2B0%3D","uGOiNrlLSjPTQvaruvraJz8XGHMNtwRLs9uYBaPofGA%3D","dLK0hehJ1MpOJk7hI6UGPW%2FWnqLW3YbSmxvY9ZDOj4s%3D","%2FNDTHnUNyFjF6nNoSYR12izLiKZJIa1im2Ba75Zg3zI%3D","znPDuXkd4F3JA4PGJjvGqTqdYFWVdw0%2BfGreYjjmTEI%3D","2jjhcoJ%2BVw1I3nsqI8eeoVdrC2FjFQXutbK82xh6z8I%3D","FsAV7mYIbnIbckxYd8Y%2BuUxeI94j2uLMOuEV7%2F6EuKQ%3D","6%2Fo5UOwwh76hJASJ%2Fj6GLkQ0%2BChuajiDox27OKxUI%2BA%3D","6oYBgrfjGBO%2FdzykEOFjtwf9wOHyXNhPgGDVLdIy8nA%3D","0ranBDC2ixRytvxT0XBXLPNpvXXyBBYxVfHKdOeO87k%3D","sLpgHpoqsB9oHnp%2BSUV3sBRVn6%2BDFBPVLqjC8rL4vDg%3D","KC%2FOtXs6yLyutfkejnDSDcmbUG8YzNToGVwTyf3ofP4%3D","8lkOBa34TTCCEb27cw5LYVmm0wZtLAovoc1ItV9kXEM%3D","8x8vYqe2jswecwchNxNgdONIdkXQUEcYN%2FsCHL%2F8YFI%3D","bhn642e55EeqbM1DJEeKec0MjrsEUjYPmyb2WQwGxwE%3D","eUef6xs2kZRbzcDazYdaWUUicWMZuZldrZpnHsRCFK8%3D","rWIoj48iWdArQ6SOl0CL3kyicprAKqp61dCf%2BlqmBvg%3D","8eHbojiAKobOHycjymvMWTuPgH%2F62GephFFUkeRRO3U%3D","aN0Cyx2wNHrge2VEtPdlEWeY7B6FrrjcI7fTLa3X9YQ%3D","tN5XjOou7aL%2BtAkv74QkJ3bAH4BdIhXVs6LB0qXAiOo%3D","ezFt5VPeIOdW%2B87X2WJlvFs6tLV55WF5BUQ4Cn7uSWc%3D","ihHvJbillYjkyzemUHZwVGofop%2Bwl02GzV0rdt0qHlI%3D","8%2BKDi3y6EcJ4utNEAA5YYSEsxCl3q9G8OJZ4BKGLZfk%3D","YVmMKj9NJ5%2FTJBy46doROQV1dnFM30XgMmv7ISuK278%3D","eYQJ4E7LqOOVYWCHWEXdPw2zbqE%2FYtG%2Ftj6fnozRc9c%3D","uYLWkbI26dvt3ZLF8BqLe2w%2B9se2n0Xs6HdQCVxSeOA%3D","axOBdG6MEu8ICnPD%2F%2BjyX22ouSSbOgXlQrjZ6rb%2F3x4%3D","FXU%2F%2FR9eE%2B3vShhp2ws9wArwyJNFGzCwkAtqXhBKRfA%3D","ZkMKb73yn70wSsuONbNLtSUHbSaf4hJy44dVRVAMAyw%3D","qoPtdDMBPgcufJGMP56fhUtzCjutyG40LXEpKQigEZ4%3D","UO4nbWq7UJQmELLyTLVnLy5LoU9Big2mmDozslCz6rY%3D","qLVgB7AG4aqvnyw1I%2FL1Ai5LoU9Big2mmDozslCz6rY%3D","hKuneRcbzegKKDGrtaff0AY3b87gh5Gg0OsS42jsrwc%3D","5xX4I03uyAxQSBZl8z%2F0GhYY%2F%2BsVsBlzhOFsIme5xgU%3D","y529Xdy0GfrPP5z9LJXnbOCYMZSbdLYLGiFA91x3%2FI8%3D","6uoazSzSU695hLyDbD%2FeMWpkcu0%2BgvvhopW2kC1Qcas%3D","IeNAg3MBxK9H6kaKCjpcIapFWkFt1aU9AUlEBs9CBKE%3D","llY6rTnxA%2FxR95OwjacgrfQr2FbtubVwvQluIUxx5Ls%3D","s2a1TAXs%2Ftps6XPS7abgfoyNDepGfbjXcDcrPHIia7k%3D","4tw0fRgUWtcAiZbe%2F3F8wdvDyEtJNQduis64%2FZK1W7w%3D","YATC7JfxlKBCvO%2Ba9t%2BTQ0IwGULRWtM8eBPtpo13XQI%3D","gxvYLKK4oBFT25PFVPjV%2BjbBuWqmompRBD9b4EshL%2B8%3D","y84crTCbA5Msb9NXf%2BfCSz%2BJrHGhCC0c1LjPvkf2QIE%3D","lWfQBNuLTyMwA4vQGFRL%2Fxt0fmUnDHk6mBZ4o5xgNO8%3D","7yEx9h4y%2BhojJShBYm%2F5udiM7%2B6shxKm7p9X6hqFpzU%3D","jGK8%2F24ZdPVIz2rg7XJk58Gnh%2BPfDhClwlThr5WKqzQ%3D","bdT%2BztO6NLXXoBoNABzXfYQix%2FGUEYKMV70OkvGEnAI%3D","mWBu1W3tam3pK6gdBypBIyQ%2BIGqr%2BwHbN8TH9KSYS%2FU%3D","xe0Pwnk3ON9Wcjnyln9%2BT3A2r7z7BmuVkVT37NGihGE%3D","a4479F2NsGYOSDTbntMKqdu2N%2By5OhqjffVhxWiTfpc%3D","2VJg6uTfJMTOV2x1XJAGqml5lsyHE1cseFlKi0J%2B2vY%3D","%2FM1CmGLg4Dzn99y19n0vOnPNxi%2BYv7Q51luJd4yTeFg%3D","RRUkDtmmEzkGRISATbXxgV2P2n332xuEMr376BMFpcM%3D","or605c6Fj83Sf2Bd4sUDr6rlrz%2BmqV8ncn1EGJfr4P0%3D","hYE9qKyy2KTgCzDX99%2BdUic3TSLexhLLKudSgyfcHOo%3D","egedDb5R%2FtnLsW9L7VOE2AlsjsrKGaW078Lbl2xRQM4%3D","oJMRqw68w7kfPk92sW0mRaj3ldlfQO8hrVWm7f8kX%2Bc%3D","ip6Y2zfJLGHCGQcZkrZ4AiUHbSaf4hJy44dVRVAMAyw%3D","ppnzwOdzI8kTECodaA9yGIyAp2PN0D0uYEd5zdBP%2Fuo%3D","sQPC8d%2FBh2hWH2EK7YPFYbsjdi9pmw4DJ2N4Z16jmgA%3D","orxZBSJUhu3Gi1wUqsb28fucadx863wSlgU%2B84LzZKA%3D","rTkEf029aYR5ILWHDdMEpQPvkt2z0lQN%2BJrYrpUl64c%3D","fhOYUi%2F8nZ0QMmSee5D6Qz8XGHMNtwRLs9uYBaPofGA%3D","JkQjmV6q9uGzVFYcknNhJxhtYGf55FVX7K3WnZZjJYA%3D","nYyFZQFYdXYbqUr6Nov02arlrz%2BmqV8ncn1EGJfr4P0%3D","PseMTkg6oQG2Q46qET1gdgee5EPzTINyXiD%2BcojJT4Y%3D","jKqVB0f77etfoh7F5P58XFS2YRGUMqVPzVaPcM2zY1Y%3D","JHaTx7mUm%2BqILq90%2FO59YtCLR88M1FxbTitpISMNLek%3D","c9U%2Fj9jn%2BQVooKqA1H9O3cVgJyBdQaBbKUAmqHarqtI%3D","n2Emze10bqVW%2Bj5xT%2BeNLzWw%2BcAcV%2FK4cjPAS3DjGtU%3D","rzZr%2BOb7ADHlyzHVKgw15zWw%2BcAcV%2FK4cjPAS3DjGtU%3D","MOtTg6EqeApP1nM50vliZvH4jq2RRG8B96yuUMpFmJ8%3D","bM4p96XgBHz4Bs2YG7lh3lwpVcTajBpL9uuEWDPvkZ8%3D","HP3lk5IKqRR%2BDL007D8mVtbnQZMOVPBoooFiD1Ddzc8%3D","f728RtUX%2BbWENGIfK6nJssJLecPsHkFPfJjT5ARvMsc%3D","pBZgiseUurMUFlRRYWChGAfpfSKpXU2Cnzl1M9rN6Ig%3D","cN4TFGqBdrQ3DCB%2BAjukJ5mKIUljs3%2B3cx6iaG3kl7g%3D","BzqSPd92KeWwUz7GmHvfat6uTKo7CsqnjHw3X9Y8lSw%3D","EHsU3xluHtGUYPlua%2BXUN7BsK8XNymSbuIhmZSoFeJQ%3D","J2sQEIudgiSZfCTOmMgrP9u381s5ixJjffKQVNGh3sc%3D","2KRZptaHCdzJ9sO1vR2OSAUFVcMubvGdgOS%2FWz4M5QM%3D","QFi%2FBNWrdD6jsgCuyo2xCtgPjru8V%2FH67husHBaFoek%3D","Ccb0Xy6xj2SgtVRder%2F9zT8XGHMNtwRLs9uYBaPofGA%3D","y34%2F0TcCggYNTZVGKLqVqIJx8mXvRyydUR6ZQ2Lz4g0%3D","934VMxnnucPjf33BWxrtopmEmi9FqrYelnmxlyizkjo%3D","jvUf55pxx3ebmAibcS%2Fg%2BfbkCytKbSpoLmOCMXznu1Q%3D","ofkyv9%2FxJ%2F7Zmh5yFsyABFXmlx54glrg3l4iIPtomEA%3D","%2BrjBS%2BXI1LGSJ%2FN8jKUBT6cYoSsGglEpIKqXmN499SE%3D","Rmfw4MZxq3oo3r5NY%2Fb6zoRGc9Ho%2BlJo1cr7cETGKbA%3D","okUz3GpEXiof8ZXOtW3UBeyRT9CJHX280pU0lDh5y4I%3D","mvhYgYii9iv8nRPE3LCsPVLmwY7I%2FK4RF8tCk9RRMH8%3D","cY8vqltEzckFds%2BV02Zmq%2BNCyXIWwMUiHh9LcnTWs%2B0%3D","KupO6l0qsBd%2F8RuGtqtlcONIdkXQUEcYN%2FsCHL%2F8YFI%3D","AUC2RAG2Er5ioxuQTPtVqZgIxI8SnyuWIrwSEpzqeVk%3D","rdQgAKnVYys%2F39Bm1NIszGEnoxYn28s371UBYCstwoM%3D","qwbChOsspceolMXrzB%2BxLlmm0wZtLAovoc1ItV9kXEM%3D","aZ0aH6KZHlfAFw6x4jMPpxIHQCEYant2vslY5o3ExlI%3D","f1Ey0Gul7HLS8Eo6bmjdsunxpGPVTcwtUl8TrQQpJSA%3D","4s3ERXWTB74pxA32SmzXKFG6nxPCWJ91dAWmBzvu4RQ%3D","JO9A%2B69y%2BeDGjUSnu%2Fvk9aizbbpCLQ4DBhIzJjm2GFc%3D","mWrAmmmHDowwMPFLBPrEJveL2jl4ptepr3PnCW%2FB0Cc%3D","AS%2BrWE%2BCiwcDnNOBCKeqI9CLR88M1FxbTitpISMNLek%3D","OJWlxDceHm%2BGG5EisPu5mDVbV0YRGSGl9uBJa%2B%2Bh8Jg%3D","APkXg8HNsRTam%2B3JbRaUD7BsK8XNymSbuIhmZSoFeJQ%3D","M18ukj64L%2FxW10WghIJ5SG2ouSSbOgXlQrjZ6rb%2F3x4%3D","U6qaXExXvcHlHl9R2VxZsiXE1ysZMx7sBge4mf4TOKk%3D","7rG2kVlSHueQ4H5KSLUf0rHtgM0Ayv0RAUiCCZBntSg%3D");
	
	@Value("${PG_RESULT_PULL_BASE_URL}")
	private String PG_RESULT_PULL_BASE_URL;
	
	public static final Logger applybodPG = LoggerFactory.getLogger("applybod-PG");
	
	@Autowired
	private MettlTeeMarksService mettlTeeMarksService;
	
	public ResponseListBean applyBodRestCall(BodBean bod)
	{
		ResponseListBean resultBean = new ResponseListBean();
		try
		{
			bod.setFileData(null);
			String url = PG_RESULT_PULL_BASE_URL+"/exam/admin/m/applyBodForTEE";
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
			HttpEntity<BodBean> requestEntity = new HttpEntity<BodBean>(bod,headers);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> response=restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
			JsonObject jsonObject = new JsonParser().parse(response.getBody()).getAsJsonObject();
			JsonElement mJson =  new JsonParser().parse(jsonObject.toString());
			Gson gson = new Gson();
			resultBean = gson.fromJson(mJson, ResponseListBean.class);
			return resultBean;
		}
		catch(Exception e)
		{
			applybodPG.info("Exception is:"+e.getMessage());
			return resultBean;
		}
	}
	
	public ResponseListBean applyBOD(List<MettlStudentTestInfo> successList, List<MettlStudentTestInfo> errorList, ArrayList<String> questionIdsList,String examYear,String examMonth ) {

		
		BasicThreadFactory factory = new BasicThreadFactory.Builder()
                .namingPattern("applybod-%d")
                .build();
		ExecutorService service = Executors.newFixedThreadPool( 10, factory ); 
		
		class ApplyBODTask implements Callable<String> {
		    private String questionId;
		     
		    ApplyBODTask(String questionId) {
		     	this.questionId = questionId; 
		     }
		    @Override
		     public String call() throws Exception{
		    		applybodPG.info(" call bod service questionId "+questionId); 	
		    		List<MettlStudentTestInfo> testInfoList = mettlTeeMarksService.grantBenefitOfDoubtToStudentsForQuestion(questionId,examYear,examMonth);
					mettlTeeMarksService.updateMarksForBenefitOfDoubtQuestions(testInfoList, successList, errorList,questionId);
					return questionId;
		     }
		 }
		
		
		List<Future<String>> allFutures = new ArrayList<>();  
		for (String questionId : questionIdsList) {
			Future<String> f = service.submit(new ApplyBODTask(questionId));
			allFutures.add(f);
		}
		
		int i = 1;
		for (Future<String> future : allFutures) {
			try {
				//System.out.println("Future result is - " +i+ " - " + future.get() + "; And Task done is " + future.isDone());
				applybodPG.info("Future result is - " +i+ " - " + future.get() + "; And Task done is " + future.isDone());
		           
			} catch (InterruptedException | ExecutionException e) {
				applybodPG.info("Exception is:"+e.getMessage());
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
			i++;
		}
		
		service.shutdown();
		System.out.println("Service Shutdown " );
		applybodPG.info("Service Shutdown " );
		
		ResponseListBean response = new ResponseListBean();
		response.setSuccessList(successList);
		response.setErrorList(errorList);
		return response;
	}

	public int insertBodQuestionIds(String examYear, String examMonth, String createdBy, List<String> questionIdsWithoutDuplicate) {
		return mettlTeeMarksService.insertBodQuestionIds(examYear, examMonth, createdBy, questionIdsWithoutDuplicate);
	}
	
	
	
}


