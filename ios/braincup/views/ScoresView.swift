//
//  FinishView.swift
//  braincup
//
//  Created by Simon Schubert on 07.10.19.
//  Copyright © 2019 Simon Schubert. All rights reserved.
//

import SwiftUI
import shared

struct ScoresView: View {
    var game: GameType
    var highscore: Int32
    var scores: [KotlinPair]
    var back: () -> Void
    
    func getScoreBarPadding(geometry: GeometryProxy, score: Int) -> CGFloat {
        return geometry.size.width - CGFloat(geometry.size.width / CGFloat((self.game.getScoreTable().get(index: 0) as! Int)+10)) * CGFloat(score)
    }
    
    var body: some View {
        NavigationView {
            VStack {
                Text(game.getName()).font(.title)
                Text("Highscore \(highscore)").font(.subheadline)
                HStack{
                    Text(">0")
                    Image("icons8-medal_third_place")
                    Text(">\((self.game.getScoreTable().get(index: 1) as! Int)-1)").padding(.leading, 16)
                    Image("icons8-medal_second_place")
                    Text(">\((self.game.getScoreTable().get(index: 0) as! Int)-1)").padding(.leading, 16)
                    Image("icons8-medal_first_place")
                }
                List {
                    ForEach(scores, id: \.self) { group in
                        Section(header: Text(group.component1() as! String)) {
                            ForEach(group.component2() as! [Int], id: \.self) { score in
                                ZStack {
                                    GeometryReader { geometry in
                                        Rectangle()
                                            .fill(Color(hex: 0xFFED7354))
                                            .padding(.trailing, self.getScoreBarPadding(geometry: geometry, score: score))
                                        HStack(alignment: .center) {
                                            Image(self.game.getMedalResource(score: Int32(score))).padding(.leading, 8)
                                            Text("\(score)").font(.headline).padding(.leading, 2).foregroundColor(.white)
                                        }.frame(height: 30)
                                    }
                                }.frame(height: 30)
                            }
                        }
                    }
                }
            }
            .navigationBarItems(leading: Button(action: { self.back()}){Image("back").foregroundColor(Color(hex: 0xFFED7354))})
        }
    }
}
