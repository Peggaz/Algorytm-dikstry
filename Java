
package algorytm.dijkstry;

import java.util.ArrayList;
import java.util.Random;
public class AlgorytmDijkstry {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Przeszukiwanie P = new Przeszukiwanie();
        P.Szukaj();
    }
}
class Przeszukiwanie{
    ArrayList<Stan> Q;//lista stanow do ekspancji
    ArrayList<Stan> H;//listat z ktora porownujemy w funkcji "czy nowa"
    Wierzcholek[] Sklepy;
    int LS=20; //liczba sklepow mksymalnie zalecane do testow max 20
    int TMax=20;//maksymalna ladownosc ciezarowki
    Transporter T = new Transporter(LS,10);
    int[] p= new int[LS]; //zapotrzebowanie dla poszczegolnych sklepow
    int[] M = new int[LS];//tablica odleglosci dla magazynu
    //ArrayList<int[]> D;//lista
Przeszukiwanie(){  
        int[][] d = new int[LS][LS+1];
        Random g = new Random();
        for(int i=0; i<LS;i++)
            for(int j=i;j<=LS; j++){
                p[i]=(20+g.nextInt(20));
                if(i==j)
                    d[i][j]=0;
                else{
                d[i][j]=(20+g.nextInt(20));
                if(j<LS)
                    d[j][i]=d[i][j];
                }
            }//wygenerowanie symetrycznej macierzy odleglosci miedzy sklepami oraz zapotrzebowania na towary
        for(int i=0; i<LS;i++){
                System.out.println();
            for(int j=0;j<LS; j++)
                System.out.print(d[i][j] + ";");//wypisanie tabel
       }
        Q = new ArrayList<>();
        H = new ArrayList<>();  
        Sklepy = new Wierzcholek[LS];
            Wierzcholek W = null;
            for(int i=0; i<LS; i++)
            {
                M[i]=d[i][LS];
                W = (new Wierzcholek(i+1,LS,d[i],p[i]));
                Sklepy[i]=W;
            }
            Stan S = new Stan(Sklepy,M,null,T, LS,0);//utworzenie stanu wyjsciowego
            Q.add(S);
            H.add(S);
    }

        
    
    void Szukaj(){
        int liczbaOperacji=0;
        while(true){
            liczbaOperacji++;
            Stan S = wybierzStanDoEkspansji();
            if(S==null)
            {
                System.out.println("brak rozwiazania");
                break;
            }
            if(czyRozwiazanie(S)){
                int krokiDoRozwiazania=0;
                int n=S.koszt;
                while(S.przodek!=null){
                    krokiDoRozwiazania++;
                    for(int i = 0; i<LS; i++)
                        System.out.println("numer Sklepu: "+S.W[i].NW+
                        "\nPotrzeba towaru: "+S.W[i].PT);
                        System.out.println("Koszt przejazdu:" + S.koszt+
                                "\nPozycaja Transporu: " + (S.Tra.pozycja + 1)+"\n\n =================================== \n\n");
                        
                S=S.przodek;
                }
                System.out.println("wykonano w: "+krokiDoRozwiazania+" krokach i w: "+liczbaOperacji+ " operacjach z clkowitym kosztem: " + n);
                break;
            }
            ArrayList<Stan> LS1 = ekspandujStan(S);
            LS1 = filtrujStany(LS1);
            uaktualnijListy(LS1);
        } 
    }
    void uaktualnijListy(ArrayList<Stan> LS1){
        for(Stan s : LS1){
            Q.add(s);
            H.add(s);
        }
    }
    ArrayList<Stan> filtrujStany(ArrayList<Stan> LS1){//wyznaczanie stanow do ekspansji i odrzucenie stanow nie rokujacych
        ArrayList<Stan> StanyOk = new ArrayList<>();
        //for(int i=0; LS.size(); i++)
        //Stan s = LS.get(i);
        for(Stan s : LS1)
            if(spelniaOgraniczenia(s) && jestNowa(s))
                StanyOk.add(s);
        return StanyOk;
    }
    boolean spelniaOgraniczenia(Stan S){//funkcja pomocnicza ktora sprawdza czy nie ma bledu w ekspansji, odrzuca stan jezeli nie spelni warunkow
        for(int i=0;i<LS; i++) 
            if(S.Tra.ladunek>TMax || S.Tra.ladunek<0 || S.W[i].PT<0)
                return false;
        return true;
    }
    boolean jestNowa(Stan S){//funkcja majaca durzy wplyw na program usuwajaca z listy poronan niepotrzebne elementy oraz odrzucajaca elementy nierokujace co znacznie zmiejsza liczbe potrzebnych operacji
        boolean t = false;
       for(int j=1; j<H.size(); j++){
           t=false;
           int n1=0, n2=0;
           Stan s = H.get(j);
           for(int i=0; i<LS; i++){
                n1+=s.W[i].PT;
                n2+=S.W[i].PT;
           }
           if(n1<=n2 && s.koszt<=S.koszt && s.Tra.pozycja == S.Tra.pozycja)//jezeli zapotrzebowanie na towar oraz koszt podrozy badanego stanu jest wiekrzy od juz istniejacego a pozycja jest taka sama to odrzuca stan
                return false;
           n1=0; 
           n2=0;
           if(n1>n2 && s.koszt>S.koszt)
               H.remove(j);//jezeli badany stan ma mniejszy koszt i mniejsze zapotrzebowanie od stanu na liscie to usuwa niepotrzebne porownanie
       }
       return true;
    }
    ArrayList<Stan> ekspandujStan(Stan S){
        Stan S1 = null;
        ArrayList<Stan> NoweStany = new ArrayList<>();        
        int i=S.Tra.pozycja;
      System.out.println(i + " "+ S.koszt);
                for(int j = 0; j<LS; j++)
                 System.out.println(i + " "+ S.koszt+ " " + S.W[j].PT);
               System.out.println("===============");//petle pomocnicze wskazujace badane obiekty
            if(S.Tra.pozycja<LS){//jezeli transporter nie jest w magazynie to:
                if(S.W[i].PT==S.Tra.ladunek){//jezeli ladunek jest rowny sklepowi to transporter trafia do magazynu a sklep zeruje zapotrzebowanie
                    Wierzcholek[] W1 = new Wierzcholek[LS];
                    for(int q=0; q<LS; q++)
                        W1[q] = new Wierzcholek(S.W[q].NW,LS,S.W[q].D,S.W[q].PT);  
                    W1[i].PT=0;
                    Transporter T1 = new Transporter(LS, 0);
                    S1 = new Stan(W1,S.Magazyn,S,T1,LS,S.koszt+S.W[i].D[LS]);
                    NoweStany.add(S1);
                }
                else if(S.W[i].PT<S.Tra.ladunek){//ladunek jest wiekszy niz zapotrzebowanie to transporter szuka optymalnej drogi do innych sklepow lub do magazynu
                    Wierzcholek[] W1 = new Wierzcholek[LS];
                    for(int q=0; q<LS; q++)
                        W1[q] = new Wierzcholek(S.W[q].NW,LS,S.W[q].D,S.W[q].PT);  
                    W1[i].PT=0;
                    Transporter T1 = null;
                    for(int j=0; j<=LS; j++)
                        if(j!=i){
                            if(j<LS)
                                if(S.W[j].PT>0){
                                    T1 = new Transporter(j, (S.Tra.ladunek-S.W[i].PT));
                                    S1 = new Stan(W1,S.Magazyn,S,T1,LS,S.koszt+S.W[i].D[j]);
                                    NoweStany.add(S1);
                                }
                            else{
                                T1 = new Transporter(j, (S.Tra.ladunek-S.W[i].PT));
                                S1 = new Stan(W1,S.Magazyn,S,T1,LS,S.koszt+S.W[i].D[j]);
                                NoweStany.add(S1);
                            }
                        }
                }
                else{//jezeli ladunek jest mniejszy niz zpotrzebowanie to transporter rusza do sklepu
                    Wierzcholek[] W1 = new Wierzcholek[LS];
                    for(int q=0; q<LS; q++)
                        W1[q] = new Wierzcholek(S.W[q].NW,LS,S.W[q].D,S.W[q].PT);            
                    W1[i].PT-=S.Tra.ladunek;
                    Transporter T1 = new Transporter(LS, 0);
                    S1=new Stan(W1,S.Magazyn,S,T1,LS,S.koszt+S.W[i].D[LS]);
                    NoweStany.add(S1); 
                }
            }
            else{//jezeli transporter jest w magazynie to laduje sie i rusza dalej
                Transporter T1 = null;
                for(int j=0; j<LS; j++)
                    if(j!=i){
                        if(S.W[j].PT>0){
                            T1 = new Transporter(j, TMax);
                            S1 = new Stan(S.W,S.Magazyn,S,T1,LS,S.koszt+S.Magazyn[j]);
                            NoweStany.add(S1);
                        }
                    }
            }       
    return NoweStany;                  
    }
    Stan wybierzStanDoEkspansji(){//funkcja wazna pod wzgledem optymalizacyjnym
        
        if(Q.isEmpty())
            return null;
        int k=10000000;
        int i1=0;
        
        for(int i=0; i<Q.size(); i++){
            if (k > Q.get(i).koszt){//jezeli zapotrzebowanie na towar stanu jest dwukrotnie mniejsze lub koszt jest mniejszy to wybierz stan
                k=Q.get(i).koszt;
                i1=i;
            }
        }
        Stan S=Q.get(i1);
        Q.remove(i1);
        return S;
    }
    boolean czyRozwiazanie(Stan S){
        for(int i=0; i<LS; i++)
            if(S.W[i].PT>0)
                return false;//sprawdza czy zapotrzebowanie jest rowne 0
        return true;
    }
}
class Wierzcholek{
    int PT;
    int[] D;
    int NW;
    Wierzcholek(int nw,int LiczbaSklepow,int[] d,int pt){
        PT = pt;
        D = d;
        NW = nw;
    }
}
class Stan{
    Wierzcholek[] W;
    int[] Magazyn;
    Stan przodek;
    Transporter Tra;
    int koszt = 0;
    Stan(Wierzcholek[] L, int[] M,Stan S,Transporter T,int LS,int k){
        koszt = k;
        W=L;
        Magazyn = M;
        przodek = S;
        Tra = T;
    }
}
class Transporter{
    int pozycja = 0;
    int ladunek = 10;
    Transporter(int p,int l){
        pozycja = p;
        ladunek = l;
    }
}
